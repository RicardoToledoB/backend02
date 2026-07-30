package com.cosam.project01.demand.service;

import com.cosam.project01.demand.dto.EpisodePurgeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class EpisodePurgeService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;

    public EpisodePurgeResponse purgeEpisode(Integer episodeId) {
        EpisodeInfo episode = findEpisode(episodeId);
        List<DocumentFile> documentFiles = findDocumentFiles(episodeId);

        Map<String, Integer> deletedRows = new TransactionTemplate(transactionManager).execute(status -> purgeDatabaseAggregate(episodeId));
        if (deletedRows == null) deletedRows = Map.of();

        FileDeletionResult fileDeletionResult = deletePhysicalFiles(episodeId, documentFiles);

        return EpisodePurgeResponse.builder()
                .episodeId(episode.id())
                .episodeCode(episode.episodeCode())
                .postulantId(episode.postulantId())
                .databasePurged(true)
                .deletedRows(deletedRows)
                .deletedFiles(fileDeletionResult.deletedFiles())
                .failedFiles(fileDeletionResult.failedFilePaths().size())
                .failedFilePaths(fileDeletionResult.failedFilePaths())
                .skippedUnsafeFilePaths(fileDeletionResult.skippedUnsafeFilePaths())
                .build();
    }

    private EpisodeInfo findEpisode(Integer episodeId) {
        List<EpisodeInfo> episodes = jdbcTemplate.query(
                """
                SELECT id, episode_code, postulant_id
                FROM episodes
                WHERE id = ?
                """,
                (rs, rowNum) -> new EpisodeInfo(
                        rs.getInt("id"),
                        rs.getString("episode_code"),
                        rs.getObject("postulant_id", Integer.class)
                ),
                episodeId
        );
        if (episodes.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "Episodio no encontrado para purga: " + episodeId);
        }
        return episodes.get(0);
    }

    private List<DocumentFile> findDocumentFiles(Integer episodeId) {
        return jdbcTemplate.query(
                """
                SELECT DISTINCT d.id, d.storage_path
                FROM episode_documents d
                WHERE d.episode_id = ?
                   OR d.event_id IN (SELECT ev.id FROM episode_events ev WHERE ev.episode_id = ?)
                   OR d.reference_id IN (SELECT er.id FROM episode_references er WHERE er.episode_id = ?)
                   OR d.stage_id IN (SELECT es.id FROM episode_stages es WHERE es.episode_id = ?)
                """,
                (rs, rowNum) -> new DocumentFile(
                        rs.getInt("id"),
                        rs.getString("storage_path")
                ),
                episodeId, episodeId, episodeId, episodeId
        );
    }

    private Map<String, Integer> purgeDatabaseAggregate(Integer episodeId) {
        Map<String, Integer> counts = new LinkedHashMap<>();

        // Rompe referencias internas/circulares del agregado antes de eliminar físicamente.
        jdbcTemplate.update("UPDATE episodes SET current_stage_id = NULL WHERE id = ?", episodeId);
        jdbcTemplate.update("UPDATE episode_events SET related_event_id = NULL WHERE episode_id = ?", episodeId);
        jdbcTemplate.update("UPDATE episode_stages SET origin_stage_id = NULL WHERE episode_id = ?", episodeId);
        jdbcTemplate.update("UPDATE episode_references SET document_id = NULL WHERE episode_id = ?", episodeId);
        jdbcTemplate.update(
                """
                UPDATE episode_documents
                SET event_id = NULL,
                    reference_id = NULL,
                    stage_id = NULL
                WHERE episode_id = ?
                   OR event_id IN (SELECT ev.id FROM episode_events ev WHERE ev.episode_id = ?)
                   OR reference_id IN (SELECT er.id FROM episode_references er WHERE er.episode_id = ?)
                   OR stage_id IN (SELECT es.id FROM episode_stages es WHERE es.episode_id = ?)
                """,
                episodeId, episodeId, episodeId, episodeId
        );

        counts.put("episode_alerts", jdbcTemplate.update(
                """
                DELETE FROM episode_alerts
                WHERE episode_id = ?
                   OR stage_id IN (SELECT es.id FROM episode_stages es WHERE es.episode_id = ?)
                """,
                episodeId, episodeId
        ));

        counts.put("episode_audit_logs", jdbcTemplate.update(
                """
                DELETE FROM episode_audit_logs
                WHERE episode_id = ?
                   OR event_id IN (SELECT ev.id FROM episode_events ev WHERE ev.episode_id = ?)
                   OR stage_id IN (SELECT es.id FROM episode_stages es WHERE es.episode_id = ?)
                """,
                episodeId, episodeId, episodeId
        ));

        counts.put("episode_documents", jdbcTemplate.update(
                """
                DELETE FROM episode_documents
                WHERE episode_id = ?
                   OR event_id IN (SELECT ev.id FROM episode_events ev WHERE ev.episode_id = ?)
                   OR reference_id IN (SELECT er.id FROM episode_references er WHERE er.episode_id = ?)
                   OR stage_id IN (SELECT es.id FROM episode_stages es WHERE es.episode_id = ?)
                """,
                episodeId, episodeId, episodeId, episodeId
        ));

        counts.put("episode_references", jdbcTemplate.update(
                "DELETE FROM episode_references WHERE episode_id = ?",
                episodeId
        ));

        counts.put("episode_events", jdbcTemplate.update(
                "DELETE FROM episode_events WHERE episode_id = ?",
                episodeId
        ));

        counts.put("episode_substances", jdbcTemplate.update(
                "DELETE FROM episode_substances WHERE episode_id = ?",
                episodeId
        ));

        counts.put("episode_stages", jdbcTemplate.update(
                "DELETE FROM episode_stages WHERE episode_id = ?",
                episodeId
        ));

        counts.put("episodes", jdbcTemplate.update(
                "DELETE FROM episodes WHERE id = ?",
                episodeId
        ));

        return counts;
    }

    private FileDeletionResult deletePhysicalFiles(Integer episodeId, List<DocumentFile> documentFiles) {
        int deleted = 0;
        Set<Path> episodeDirectories = new LinkedHashSet<>();
        List<String> failed = new ArrayList<>();
        List<String> skippedUnsafe = new ArrayList<>();

        for (DocumentFile documentFile : documentFiles) {
            String storagePath = documentFile.storagePath();
            if (storagePath == null || storagePath.isBlank()) {
                continue;
            }

            Path path;
            try {
                path = Paths.get(storagePath).toAbsolutePath().normalize();
            } catch (Exception ex) {
                skippedUnsafe.add(storagePath);
                continue;
            }

            if (!isSafeEpisodeDocumentPath(path, episodeId)) {
                skippedUnsafe.add(path.toString());
                continue;
            }

            Path parent = path.getParent();
            if (parent != null) episodeDirectories.add(parent);

            try {
                if (Files.deleteIfExists(path)) {
                    deleted++;
                }
            } catch (IOException | SecurityException ex) {
                log.warn("No se pudo eliminar archivo físico del episodio {}: {}", episodeId, path, ex);
                failed.add(path.toString());
            }
        }

        for (Path directory : episodeDirectories) {
            tryDeleteEmptyDirectory(directory);
        }

        return new FileDeletionResult(deleted, failed, skippedUnsafe);
    }

    private boolean isSafeEpisodeDocumentPath(Path path, Integer episodeId) {
        String normalized = path.toString().replace('\\', '/');
        return normalized.contains("/documents/episodes/" + episodeId + "/");
    }

    private void tryDeleteEmptyDirectory(Path directory) {
        try {
            if (Files.isDirectory(directory) && isDirectoryEmpty(directory)) {
                Files.deleteIfExists(directory);
            }
        } catch (IOException | SecurityException ex) {
            log.warn("No se pudo eliminar directorio vacío de documentos: {}", directory, ex);
        }
    }

    private boolean isDirectoryEmpty(Path directory) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            return !stream.iterator().hasNext();
        }
    }

    private record EpisodeInfo(Integer id, String episodeCode, Integer postulantId) {}
    private record DocumentFile(Integer id, String storagePath) {}
    private record FileDeletionResult(Integer deletedFiles, List<String> failedFilePaths, List<String> skippedUnsafeFilePaths) {}
}
