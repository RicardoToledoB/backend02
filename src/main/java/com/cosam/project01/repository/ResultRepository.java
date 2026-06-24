package com.cosam.project01.repository;

import com.cosam.project01.entity.ResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<ResultEntity,Integer> {

    Optional<ResultEntity> findByCodeIgnoreCase(String code);
    Optional<ResultEntity> findByNameIgnoreCase(String name);

    @Query(
            value = "SELECT * FROM results c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<ResultEntity> findAllDeleted();

    @Query("SELECT ur FROM ResultEntity ur WHERE ur.deletedAt IS NULL")
    List<ResultEntity> findAllActive();

    @Query(value = "SELECT * FROM results c WHERE c.id = :id", nativeQuery = true)
    Optional<ResultEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM results", nativeQuery = true)
    List<ResultEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM ResultEntity c")
    Page<ResultEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM ResultEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))
              OR LOWER(c.code) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<ResultEntity> search(@Param("name") String name, Pageable pageable);
}

