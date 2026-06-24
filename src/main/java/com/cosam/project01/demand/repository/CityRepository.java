package com.cosam.project01.demand.repository;

import com.cosam.project01.demand.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity, Integer> {
    Optional<CityEntity> findByCodeIgnoreCase(String code);
    List<CityEntity> findByRegion_Id(Integer regionId);
}
