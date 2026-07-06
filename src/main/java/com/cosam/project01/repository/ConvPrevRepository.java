package com.cosam.project01.repository;

import com.cosam.project01.entity.ConvPrevEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConvPrevRepository extends JpaRepository<ConvPrevEntity, Integer> {

    @Query("""
       SELECT c FROM ConvPrevEntity c
       WHERE c.deletedAt IS NULL
         AND COALESCE(c.active, true) = true
       ORDER BY c.id ASC
    """)
    List<ConvPrevEntity> findAllActive();

    @Query(value = "SELECT * FROM conv_prevs WHERE deleted_at IS NOT NULL ORDER BY id", nativeQuery = true)
    List<ConvPrevEntity> findAllDeleted();

    @Query(value = "SELECT * FROM conv_prevs ORDER BY id", nativeQuery = true)
    List<ConvPrevEntity> findAllIncludingDeleted();

    @Query(value = "SELECT * FROM conv_prevs WHERE id = :id", nativeQuery = true)
    Optional<ConvPrevEntity> findAnyById(@Param("id") Integer id);

    @Query("""
       SELECT c FROM ConvPrevEntity c
       WHERE c.deletedAt IS NULL
         AND (:q IS NULL OR TRIM(:q) = ''
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(c.code) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(c.description) LIKE LOWER(CONCAT('%', :q, '%')))
         AND (:intPrevId IS NULL OR c.intPrev.id = :intPrevId)
    """)
    Page<ConvPrevEntity> search(@Param("q") String q,
                                @Param("intPrevId") Integer intPrevId,
                                Pageable pageable);
}
