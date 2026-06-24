package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.AttendanceStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AttendanceStatusRepository extends JpaRepository<AttendanceStatusEntity, Integer> {
    Optional<AttendanceStatusEntity> findByCodeIgnoreCase(String code);
}
