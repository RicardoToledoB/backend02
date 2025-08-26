package com.cosam.project01.repository;

import com.cosam.project01.entity.DiverterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiverterRepository extends JpaRepository<DiverterEntity,Integer> {

    @Query(
            value = "SELECT * FROM diverters c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<DiverterEntity> findAllDeleted();

    @Query("SELECT ur FROM DiverterEntity ur WHERE ur.deletedAt IS NULL")
    List<DiverterEntity> findAllActive();

    @Query(value = "SELECT * FROM diverters c WHERE c.id = :id", nativeQuery = true)
    Optional<DiverterEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM diverters", nativeQuery = true)
    List<DiverterEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM DiverterEntity c")
    Page<DiverterEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM DiverterEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<DiverterEntity> search(@Param("name") String name, Pageable pageable);

}
