package com.cosam.project01.repository;

import com.cosam.project01.entity.MovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovementRepository extends JpaRepository<MovementEntity,Integer> {

    @Query(
            value = "SELECT * FROM movements c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<MovementEntity> findAllDeleted();

    @Query("SELECT ur FROM MovementEntity ur WHERE ur.deletedAt IS NULL")
    List<MovementEntity> findAllActive();

    @Query(value = "SELECT * FROM movements c WHERE c.id = :id", nativeQuery = true)
    Optional<MovementEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM movements", nativeQuery = true)
    List<MovementEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM MovementEntity c")
    Page<MovementEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM MovementEntity c
       WHERE (:id IS NULL OR CAST(c.id AS string) LIKE CONCAT('%', :id, '%'))
""")
    Page<MovementEntity> search(@Param("id") String id, Pageable pageable);
}

