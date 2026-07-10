package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EpisodeEventRepository extends JpaRepository<EpisodeEventEntity, Integer> {
    List<EpisodeEventEntity> findByEpisodeIdOrderByEventDateAscEventTimeAscIdAsc(Integer episodeId);
    List<EpisodeEventEntity> findByStageIdOrderByEventDateAscEventTimeAscIdAsc(Integer stageId);
    boolean existsByEpisodeIdAndEventType_CodeIgnoreCase(Integer episodeId, String eventTypeCode);

    @Query("""
            SELECT COUNT(ev) FROM EpisodeEventEntity ev
            WHERE ev.stage.id = :stageId
              AND UPPER(ev.eventType.code) IN ('ASISTENCIA', 'CITACION')
              AND UPPER(ev.attendanceStatus.code) = 'NO_SE_PRESENTO'
              AND (:professionalUserId IS NULL OR ev.professionalUser.id = :professionalUserId)
            """)
    long countNoShowByStageAndProfessional(@Param("stageId") Integer stageId,
                                            @Param("professionalUserId") Integer professionalUserId);

    @Query("""
            SELECT COUNT(ev) FROM EpisodeEventEntity ev
            WHERE ev.stage.id = :stageId
              AND UPPER(ev.eventType.code) IN ('ASISTENCIA', 'CITACION')
              AND UPPER(ev.attendanceStatus.code) = 'NO_SE_PRESENTO'
              AND ev.programProfessional.id = :programProfessionalId
            """)
    long countNoShowByStageAndProgramProfessional(@Param("stageId") Integer stageId,
                                                  @Param("programProfessionalId") Long programProfessionalId);
}
