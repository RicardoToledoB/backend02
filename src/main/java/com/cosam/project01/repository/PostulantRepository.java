package com.cosam.project01.repository;

import com.cosam.project01.entity.PostulantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostulantRepository extends JpaRepository<PostulantEntity,Integer> {

    @Query(
            value = "SELECT * FROM postulants c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<PostulantEntity> findAllDeleted();

    @Query("SELECT ur FROM PostulantEntity ur WHERE ur.deletedAt IS NULL")
    List<PostulantEntity> findAllActive();

    @Query(value = "SELECT * FROM postulants c WHERE c.id = :id", nativeQuery = true)
    Optional<PostulantEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM postulants", nativeQuery = true)
    List<PostulantEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM PostulantEntity c")
    Page<PostulantEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM PostulantEntity c
       WHERE (:rut IS NULL OR TRIM(:rut) = '' 
              OR LOWER(c.rut) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<PostulantEntity> search(@Param("rut") String rut, Pageable pageable);

    @Query("""
        SELECT p FROM PostulantEntity p
        WHERE (:rut IS NULL OR TRIM(:rut) = '' 
               OR LOWER(p.rut) LIKE LOWER(CONCAT('%', :rut, '%')))
    """)
    Page<PostulantEntity> searchByRut(@Param("rut") String rut, Pageable pageable);

}
