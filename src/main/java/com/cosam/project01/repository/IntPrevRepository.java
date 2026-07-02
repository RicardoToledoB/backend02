package com.cosam.project01.repository;

import com.cosam.project01.entity.IntPrevEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntPrevRepository extends JpaRepository<IntPrevEntity, Integer> {

    @Query(value = "SELECT * FROM int_prevs WHERE deleted_at IS NOT NULL ORDER BY id", nativeQuery = true)
    List<IntPrevEntity> findAllDeleted();

    @Query(value = "SELECT * FROM int_prevs ORDER BY id", nativeQuery = true)
    List<IntPrevEntity> findAllIncludingDeleted();

    @Query(value = "SELECT * FROM int_prevs WHERE id = :id", nativeQuery = true)
    Optional<IntPrevEntity> findAnyById(@Param("id") Integer id);

    @Query("""
       SELECT p FROM IntPrevEntity p
       WHERE (:q IS NULL OR TRIM(:q) = ''
              OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.code) LIKE LOWER(CONCAT('%', :q, '%')))
    """)
    Page<IntPrevEntity> search(@Param("q") String q, Pageable pageable);
}
