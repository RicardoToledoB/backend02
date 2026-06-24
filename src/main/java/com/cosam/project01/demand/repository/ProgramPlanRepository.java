package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.ProgramPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProgramPlanRepository extends JpaRepository<ProgramPlanEntity, Integer> {
    Optional<ProgramPlanEntity> findByCodeIgnoreCase(String code);
}
