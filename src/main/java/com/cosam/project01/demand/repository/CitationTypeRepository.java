package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.CitationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CitationTypeRepository extends JpaRepository<CitationTypeEntity, Integer> {
    Optional<CitationTypeEntity> findByCodeIgnoreCase(String code);
    List<CitationTypeEntity> findByActiveTrueOrderBySortOrderAscNameAsc();
}
