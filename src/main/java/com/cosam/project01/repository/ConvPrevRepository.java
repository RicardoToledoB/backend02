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
public interface ConvPrevRepository extends JpaRepository<ConvPrevEntity,Integer> {

    @Query(
            value = "SELECT * FROM conv_prevs c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<ConvPrevEntity> findAllDeleted();

    @Query("SELECT ur FROM ConvPrevEntity ur WHERE ur.deletedAt IS NULL")
    List<ConvPrevEntity> findAllActive();

    @Query(value = "SELECT * FROM conv_prevs c WHERE c.id = :id", nativeQuery = true)
    Optional<ConvPrevEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM conv_prevs", nativeQuery = true)
    List<ConvPrevEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM ConvPrevEntity c")
    Page<ConvPrevEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM ConvPrevEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<ConvPrevEntity> search(@Param("name") String name, Pageable pageable);
}

