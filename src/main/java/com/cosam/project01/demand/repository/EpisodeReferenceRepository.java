package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeReferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EpisodeReferenceRepository extends JpaRepository<EpisodeReferenceEntity, Integer> {
    List<EpisodeReferenceEntity> findByEpisodeIdOrderByReferenceDateAsc(Integer episodeId);
    long countByEpisodeId(Integer episodeId);
}
