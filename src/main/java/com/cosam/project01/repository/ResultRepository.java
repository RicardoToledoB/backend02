package com.cosam.project01.repository;

import com.cosam.project01.entity.ResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Query(value = """
            SELECT * FROM results c
            WHERE c.id <> :id
              AND c.deleted_at IS NULL
              AND UPPER(c.code) = UPPER(:code)
            LIMIT 1
            """, nativeQuery = true)
    Optional<ResultEntity> findActiveDuplicateCode(@Param("id") Integer id, @Param("code") String code);

    /**
     * Si un código quedó ocupado por un duplicado eliminado lógicamente, MySQL
     * igual mantiene la restricción UNIQUE. Antes de actualizar el registro
     * histórico/base, liberamos el código de los eliminados para evitar error.
     */
    @Transactional
    @Modifying
    @Query(value = """
            UPDATE results
            SET code = CONCAT('DELETED_', id, '_', code)
            WHERE id <> :id
              AND deleted_at IS NOT NULL
              AND code IS NOT NULL
              AND UPPER(code) = UPPER(:code)
            """, nativeQuery = true)
    int neutralizeDeletedDuplicatesByCode(@Param("id") Integer id, @Param("code") String code);

    @Transactional
    @Modifying
    @Query(value = "UPDATE results SET deleted_at = CURRENT_TIMESTAMP, active = false WHERE id = :id", nativeQuery = true)
    int softDeleteAndDeactivate(@Param("id") Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE results SET deleted_at = NULL, active = true WHERE id = :id", nativeQuery = true)
    int restoreAndActivate(@Param("id") Integer id);

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

