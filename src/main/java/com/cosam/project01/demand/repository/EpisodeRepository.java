package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<EpisodeEntity, Integer> {

    Optional<EpisodeEntity> findByEpisodeCodeIgnoreCase(String episodeCode);

    @Query("""
            SELECT e FROM EpisodeEntity e
            WHERE e.postulant.id = :postulantId
              AND e.active = true
              AND e.deletedAt IS NULL
            ORDER BY e.createdAt DESC
            """)
    Optional<EpisodeEntity> findActiveByPostulantId(@Param("postulantId") Integer postulantId);

    @Query("""
            SELECT e FROM EpisodeEntity e
            WHERE (LOWER(e.postulant.rut) = LOWER(:rut)
                   OR LOWER(REPLACE(REPLACE(e.postulant.rut, '.', ''), '-', '')) = LOWER(REPLACE(REPLACE(:rut, '.', ''), '-', '')))
              AND e.active = true
              AND e.deletedAt IS NULL
            ORDER BY e.createdAt DESC
            """)
    Optional<EpisodeEntity> findActiveByRut(@Param("rut") String rut);

    @Query("""
            SELECT e FROM EpisodeEntity e
            WHERE (LOWER(e.postulant.rut) = LOWER(:rut)
                   OR LOWER(REPLACE(REPLACE(e.postulant.rut, '.', ''), '-', '')) = LOWER(REPLACE(REPLACE(:rut, '.', ''), '-', '')))
              AND e.deletedAt IS NULL
            ORDER BY e.originalRequestDate DESC, e.id DESC
            """)
    List<EpisodeEntity> findHistoryByRut(@Param("rut") String rut);

    @Query("""
            SELECT e FROM EpisodeEntity e
            WHERE e.postulant.id = :postulantId
              AND e.deletedAt IS NULL
            ORDER BY e.originalRequestDate DESC, e.id DESC
            """)
    List<EpisodeEntity> findHistoryByPostulantId(@Param("postulantId") Integer postulantId);

    @Query("""
            SELECT e FROM EpisodeEntity e
            LEFT JOIN e.currentStage cs
            WHERE e.deletedAt IS NULL
              AND (
                    UPPER(COALESCE(:stateCode, '')) = 'CERRADO'
                    OR (e.active = true AND e.closedAt IS NULL)
              )
              AND (:programId IS NULL OR e.currentProgram.id = :programId)
              AND (:stateCode IS NULL OR :stateCode = '' OR UPPER(COALESCE(cs.stateCode, e.stateCode)) = UPPER(:stateCode))
              AND (:resultCode IS NULL OR :resultCode = '' OR UPPER(COALESCE(cs.resultCode, e.resultCode)) = UPPER(:resultCode))
            ORDER BY e.originalRequestDate ASC, e.id ASC
            """)
    Page<EpisodeEntity> findPrioritized(@Param("programId") Integer programId,
                                        @Param("stateCode") String stateCode,
                                        @Param("resultCode") String resultCode,
                                        Pageable pageable);


    @Query("""
            SELECT COUNT(e) FROM EpisodeEntity e
            WHERE e.active = true
              AND e.deletedAt IS NULL
              AND NOT EXISTS (
                    SELECT ev FROM EpisodeEventEntity ev
                    WHERE ev.episode = e
                      AND UPPER(ev.eventType.code) = 'CITACION'
              )
            """)
    long countActiveWithoutFirstCitation();

    long countByActiveTrueAndDeletedAtIsNull();
    long countByActiveTrueAndResultCodeAndDeletedAtIsNull(String resultCode);
}
