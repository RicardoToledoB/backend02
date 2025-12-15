package com.cosam.project01.repository;

import com.cosam.project01.entity.RegisterSubstanceEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterSubstanceRepository extends JpaRepository<RegisterSubstanceEntity,Integer> {


    @Query(
            value = "SELECT * FROM registers_substances c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<RegisterSubstanceEntity> findAllDeleted();

    @Query("SELECT ur FROM RegisterSubstanceEntity ur WHERE ur.deletedAt IS NULL")
    List<RegisterSubstanceEntity> findAllActive();

    @Query(value = "SELECT * FROM registers_substances c WHERE c.id = :id", nativeQuery = true)
    Optional<RegisterSubstanceEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM registers_substances", nativeQuery = true)
    List<RegisterSubstanceEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM RegisterSubstanceEntity c")
    Page<RegisterSubstanceEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM RegisterSubstanceEntity c
       WHERE (:id IS NULL OR c.id = :id)
    """)
    Page<RegisterSubstanceEntity> search(@Param("id") Integer id, Pageable pageable);

    @Query("""
    SELECT c FROM RegisterSubstanceEntity c
    WHERE (:registerId IS NULL OR c.register.id = :registerId)
      AND c.deletedAt IS NULL
""")
    Page<RegisterSubstanceEntity> searchByRegisterId(@Param("registerId") Integer registerId, Pageable pageable);


    @Modifying
    @Transactional
    @Query("""
        UPDATE RegisterSubstanceEntity c
           SET c.deletedAt = CURRENT_TIMESTAMP
         WHERE c.register.id = :registerId
           AND c.deletedAt IS NULL
    """)
    int softDeleteByRegisterId(@Param("registerId") Integer registerId);

}

