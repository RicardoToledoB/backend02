package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.ClosureReasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClosureReasonRepository extends JpaRepository<ClosureReasonEntity, Integer> {
    Optional<ClosureReasonEntity> findByCodeIgnoreCase(String code);

    List<ClosureReasonEntity> findByActiveTrueOrderByNameAsc();
}
