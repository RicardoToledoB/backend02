package com.cosam.project01.repository;

import com.cosam.project01.entity.SexEntity;
import com.cosam.project01.entity.SexEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SexRepository extends JpaRepository<SexEntity,Integer> {


    @Query(
            value = "SELECT * FROM sexs c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<SexEntity> findAllDeleted();

    @Query("SELECT ur FROM SexEntity ur WHERE ur.deletedAt IS NULL")
    List<SexEntity> findAllActive();

    @Query(value = "SELECT * FROM sexs c WHERE c.id = :id", nativeQuery = true)
    Optional<SexEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM sexs", nativeQuery = true)
    List<SexEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM SexEntity c")
    Page<SexEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM SexEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<SexEntity> search(@Param("name") String name, Pageable pageable);
}
