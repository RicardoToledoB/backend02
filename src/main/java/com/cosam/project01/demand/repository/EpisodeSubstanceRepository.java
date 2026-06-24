package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeSubstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EpisodeSubstanceRepository extends JpaRepository<EpisodeSubstanceEntity, Integer> {
    List<EpisodeSubstanceEntity> findByEpisodeId(Integer episodeId);
}
