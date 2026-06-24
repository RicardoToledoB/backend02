package com.cosam.project01.repository;

import com.cosam.project01.entity.StateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<StateEntity, Integer> {

    Optional<StateEntity> findByCodeIgnoreCase(String code);
    Optional<StateEntity> findByNameIgnoreCase(String name);

    @Query(value = "SELECT * FROM states c WHERE c.deleted_at IS NOT NULL", nativeQuery = true)
    List<StateEntity> findAllDeleted();

    @Query("SELECT c FROM StateEntity c WHERE c.deletedAt IS NULL")
    List<StateEntity> findAllActive();

    @Query(value = "SELECT * FROM states c WHERE c.id = :id", nativeQuery = true)
    Optional<StateEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM states", nativeQuery = true)
    List<StateEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM StateEntity c")
    Page<StateEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM StateEntity c
       WHERE (:search IS NULL OR TRIM(:search) = ''
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(c.scope) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<StateEntity> search(@Param("search") String search, Pageable pageable);
}
