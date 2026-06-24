package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EpisodeAlertRepository extends JpaRepository<EpisodeAlertEntity, Integer> {
    List<EpisodeAlertEntity> findByEpisodeIdOrderByCreatedAtDesc(Integer episodeId);
    long countByStatusCodeIgnoreCaseAndDeletedAtIsNull(String statusCode);
}
