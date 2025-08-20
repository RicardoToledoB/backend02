package com.cosam.project01.repository;

import com.cosam.project01.entity.CommuneEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommuneRepository extends JpaRepository<CommuneEntity,Integer> {

    @Query(
            value = "SELECT * FROM communes c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<CommuneEntity> findAllDeleted();

    @Query("SELECT ur FROM CommuneEntity ur WHERE ur.deletedAt IS NULL")
    List<CommuneEntity> findAllActive();

    @Query(value = "SELECT * FROM communes c WHERE c.id = :id", nativeQuery = true)
    Optional<CommuneEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM communes", nativeQuery = true)
    List<CommuneEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM CommuneEntity c")
    Page<CommuneEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM CommuneEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<CommuneEntity> search(@Param("name") String name, Pageable pageable);
}

