package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.DocumentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentTypeRepository extends JpaRepository<DocumentTypeEntity, Integer> {
    Optional<DocumentTypeEntity> findByCodeIgnoreCase(String code);
}
