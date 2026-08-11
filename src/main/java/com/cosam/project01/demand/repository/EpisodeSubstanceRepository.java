package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeSubstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EpisodeSubstanceRepository extends JpaRepository<EpisodeSubstanceEntity, Integer> {
    List<EpisodeSubstanceEntity> findByEpisodeId(Integer episodeId);
    List<EpisodeSubstanceEntity> findByEpisodeIdOrderByUseOrderAscIdAsc(Integer episodeId);
    Optional<EpisodeSubstanceEntity> findByEpisodeIdAndSubstanceId(Integer episodeId, Integer substanceId);
    Optional<EpisodeSubstanceEntity> findByEpisodeIdAndSubstanceIdAndIdNot(Integer episodeId, Integer substanceId, Integer id);
    List<EpisodeSubstanceEntity> findByEpisodeIdAndPrimarySubstanceTrue(Integer episodeId);
}
