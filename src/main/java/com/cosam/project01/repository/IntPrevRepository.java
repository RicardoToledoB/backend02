package com.cosam.project01.repository;

import com.cosam.project01.entity.IntPrevEntity;
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
public interface IntPrevRepository extends JpaRepository<IntPrevEntity,Integer> {

    @Query(
            value = "SELECT * FROM int_prevs c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<IntPrevEntity> findAllDeleted();

    @Query("SELECT ur FROM IntPrevEntity ur WHERE ur.deletedAt IS NULL")
    List<IntPrevEntity> findAllActive();

    @Query(value = "SELECT * FROM int_prevs c WHERE c.id = :id", nativeQuery = true)
    Optional<IntPrevEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM int_prevs", nativeQuery = true)
    List<IntPrevEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM IntPrevEntity c")
    Page<IntPrevEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM IntPrevEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<IntPrevEntity> search(@Param("name") String name, Pageable pageable);
}

