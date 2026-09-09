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



    @Query("""
            SELECT s FROM EpisodeStageEntity s
            JOIN FETCH s.episode e
            LEFT JOIN FETCH e.postulant p
            LEFT JOIN FETCH e.createdByUser createdBy
            LEFT JOIN FETCH e.currentProgram currentProgram
            LEFT JOIN FETCH e.currentStage currentStage
            LEFT JOIN FETCH s.program stageProgram
            LEFT JOIN FETCH s.originStage originStage
            LEFT JOIN FETCH s.closureReason closureReason
            LEFT JOIN FETCH s.responsibleUser responsibleUser
            WHERE (:programId IS NULL OR stageProgram.id = :programId)
              AND (:stateCode IS NULL OR UPPER(s.stateCode) = UPPER(:stateCode))
              AND (:resultCode IS NULL OR UPPER(s.resultCode) = UPPER(:resultCode))
            ORDER BY e.originalRequestDate ASC, e.id ASC, s.stageOrder ASC, s.id ASC
            """)
    List<EpisodeStageEntity> findPrioritizedStageRows(@Param("programId") Integer programId,
                                                       @Param("stateCode") String stateCode,
                                                       @Param("resultCode") String resultCode);

    @Query("SELECT COALESCE(MAX(s.stageOrder), 0) FROM EpisodeStageEntity s WHERE s.episode.id = :episodeId")
    Integer findMaxStageOrder(@Param("episodeId") Integer episodeId);
}
