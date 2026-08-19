package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeStageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EpisodeStageRepository extends JpaRepository<EpisodeStageEntity, Integer> {
    List<EpisodeStageEntity> findByEpisodeIdOrderByStageOrderAsc(Integer episodeId);
    Optional<EpisodeStageEntity> findFirstByEpisodeIdAndCurrentTrueOrderByStageOrderDesc(Integer episodeId);
    Optional<EpisodeStageEntity> findFirstByEpisodeIdAndProgramIdOrderByStageOrderDescIdDesc(Integer episodeId, Integer programId);

    @Query("SELECT COALESCE(MAX(s.stageOrder), 0) FROM EpisodeStageEntity s WHERE s.episode.id = :episodeId")
    Integer findMaxStageOrder(@Param("episodeId") Integer episodeId);
}
