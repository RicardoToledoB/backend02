package com.cosam.project01.repository;

import com.cosam.project01.entity.ProfessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionRepository extends JpaRepository<ProfessionEntity,Integer> {

    @Query(
            value = "SELECT * FROM professions c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<ProfessionEntity> findAllDeleted();

    @Query("SELECT ur FROM ProfessionEntity ur WHERE ur.deletedAt IS NULL")
    List<ProfessionEntity> findAllActive();

    @Query(value = "SELECT * FROM professions c WHERE c.id = :id", nativeQuery = true)
    Optional<ProfessionEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM professions", nativeQuery = true)
    List<ProfessionEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM ProfessionEntity c")
    Page<ProfessionEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM ProfessionEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<ProfessionEntity> search(@Param("name") String name, Pageable pageable);
}

