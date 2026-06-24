package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.ProgramPopulationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProgramPopulationRepository extends JpaRepository<ProgramPopulationEntity, Integer> {
    Optional<ProgramPopulationEntity> findByCodeIgnoreCase(String code);
}
