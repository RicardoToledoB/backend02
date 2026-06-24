package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EpisodeDocumentRepository extends JpaRepository<EpisodeDocumentEntity, Integer> {
    List<EpisodeDocumentEntity> findByEpisodeIdOrderByUploadedAtDesc(Integer episodeId);
}
