package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.ProgramModalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProgramModalityRepository extends JpaRepository<ProgramModalityEntity, Integer> {
    Optional<ProgramModalityEntity> findByCodeIgnoreCase(String code);
}
