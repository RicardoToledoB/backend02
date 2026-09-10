package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeReferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EpisodeReferenceRepository extends JpaRepository<EpisodeReferenceEntity, Integer> {
    List<EpisodeReferenceEntity> findByEpisodeIdOrderByReferenceDateAsc(Integer episodeId);
    List<EpisodeReferenceEntity> findByEpisodeIdAndOriginStageIdOrderByReferenceDateAsc(Integer episodeId, Integer originStageId);
    Optional<EpisodeReferenceEntity> findFirstByDestinationStageIdOrderByReferenceDateDesc(Integer destinationStageId);
    long countByEpisodeId(Integer episodeId);
}
