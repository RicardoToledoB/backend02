package com.cosam.project01.repository;

import com.cosam.project01.entity.RegisterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterRepository extends JpaRepository<RegisterEntity,Integer> {

    @Query(
            value = "SELECT * FROM registers c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<RegisterEntity> findAllDeleted();

    @Query("SELECT ur FROM RegisterEntity ur WHERE ur.deletedAt IS NULL")
    List<RegisterEntity> findAllActive();

    @Query(value = "SELECT * FROM registers c WHERE c.id = :id", nativeQuery = true)
    Optional<RegisterEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM registers", nativeQuery = true)
    List<RegisterEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM RegisterEntity c")
    Page<RegisterEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM RegisterEntity c
       WHERE (:description IS NULL OR TRIM(:description) = '' 
              OR LOWER(c.description) LIKE LOWER(CONCAT('%', :description, '%')))
    """)
    Page<RegisterEntity> search(@Param("description") String description, Pageable pageable);

}
