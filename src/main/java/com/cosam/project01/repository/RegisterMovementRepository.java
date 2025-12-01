package com.cosam.project01.repository;

import com.cosam.project01.entity.RegisterMovementEntity;
import com.cosam.project01.entity.RegisterMovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterMovementRepository extends JpaRepository<RegisterMovementEntity,Integer> {

    @Query(
            value = "SELECT * FROM registers_movements c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<RegisterMovementEntity> findAllDeleted();

    @Query("SELECT ur FROM RegisterMovementEntity ur WHERE ur.deletedAt IS NULL")
    List<RegisterMovementEntity> findAllActive();

    @Query(value = "SELECT * FROM registers_movements c WHERE c.id = :id", nativeQuery = true)
    Optional<RegisterMovementEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM registers_movements", nativeQuery = true)
    List<RegisterMovementEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM RegisterMovementEntity c")
    Page<RegisterMovementEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM RegisterMovementEntity c
       WHERE (:full_name IS NULL OR TRIM(:full_name) = '' 
              OR LOWER(c.full_name) LIKE LOWER(CONCAT('%', :full_name, '%')))
    """)
    Page<RegisterMovementEntity> search(@Param("full_name") String full_name, Pageable pageable);

    @Query("""
    SELECT c FROM RegisterMovementEntity c
    WHERE (:registerId IS NULL OR c.register.id = :registerId)
      AND c.deletedAt IS NULL
""")
    Page<RegisterMovementEntity> searchByRegisterId(@Param("registerId") Integer registerId, Pageable pageable);

}
