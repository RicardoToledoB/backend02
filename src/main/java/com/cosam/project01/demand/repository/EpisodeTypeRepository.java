package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EpisodeTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EpisodeTypeRepository extends JpaRepository<EpisodeTypeEntity, Integer> {
    Optional<EpisodeTypeEntity> findByCodeIgnoreCase(String code);
}
