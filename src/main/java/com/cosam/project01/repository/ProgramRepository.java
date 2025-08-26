package com.cosam.project01.repository;

import com.cosam.project01.entity.ProgramEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<ProgramEntity,Integer> {

    @Query(
            value = "SELECT * FROM programs c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<ProgramEntity> findAllDeleted();

    @Query("SELECT ur FROM ProgramEntity ur WHERE ur.deletedAt IS NULL")
    List<ProgramEntity> findAllActive();

    @Query(value = "SELECT * FROM programs c WHERE c.id = :id", nativeQuery = true)
    Optional<ProgramEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM programs", nativeQuery = true)
    List<ProgramEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM ProgramEntity c")
    Page<ProgramEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM ProgramEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<ProgramEntity> search(@Param("name") String name, Pageable pageable);

}
