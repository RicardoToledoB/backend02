package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeAuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EpisodeAuditLogRepository extends JpaRepository<EpisodeAuditLogEntity, Integer> {
    List<EpisodeAuditLogEntity> findByEpisodeIdOrderByPerformedAtDesc(Integer episodeId);
}
