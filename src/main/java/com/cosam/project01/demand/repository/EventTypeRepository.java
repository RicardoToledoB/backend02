package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.EventTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EventTypeRepository extends JpaRepository<EventTypeEntity, Integer> {
    Optional<EventTypeEntity> findByCodeIgnoreCase(String code);
}
