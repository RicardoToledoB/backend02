package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.SemaphoreRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SemaphoreRuleRepository extends JpaRepository<SemaphoreRuleEntity, Integer> {
    Optional<SemaphoreRuleEntity> findByColorCodeIgnoreCase(String colorCode);
    List<SemaphoreRuleEntity> findByActiveTrueOrderByMinDaysAsc();
}
