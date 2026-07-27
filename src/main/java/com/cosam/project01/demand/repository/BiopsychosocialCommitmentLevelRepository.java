package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.BiopsychosocialCommitmentLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BiopsychosocialCommitmentLevelRepository extends JpaRepository<BiopsychosocialCommitmentLevelEntity, Integer> {
    Optional<BiopsychosocialCommitmentLevelEntity> findByCodeIgnoreCase(String code);
    List<BiopsychosocialCommitmentLevelEntity> findByActiveTrueOrderByNameAsc();
}
