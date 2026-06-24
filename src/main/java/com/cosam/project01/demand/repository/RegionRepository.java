package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<RegionEntity, Integer> {
    Optional<RegionEntity> findByCodeIgnoreCase(String code);
}
