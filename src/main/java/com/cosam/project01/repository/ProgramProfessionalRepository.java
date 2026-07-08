package com.cosam.project01.repository;

import com.cosam.project01.entity.ProgramProfessionalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramProfessionalRepository extends JpaRepository<ProgramProfessionalEntity, Long> {

    @Query("""
       SELECT pp FROM ProgramProfessionalEntity pp
       WHERE pp.deletedAt IS NULL
         AND COALESCE(pp.active, true) = true
       ORDER BY pp.id ASC
    """)
    List<ProgramProfessionalEntity> findAllActive();

    @Query(value = "SELECT * FROM program_professionals WHERE deleted_at IS NOT NULL ORDER BY id", nativeQuery = true)
    List<ProgramProfessionalEntity> findAllDeleted();

    @Query(value = "SELECT * FROM program_professionals ORDER BY id", nativeQuery = true)
    List<ProgramProfessionalEntity> findAllIncludingDeleted();

    @Query(value = "SELECT * FROM program_professionals WHERE id = :id", nativeQuery = true)
    Optional<ProgramProfessionalEntity> findAnyById(@Param("id") Long id);

    @Query("""
       SELECT DISTINCT pp FROM ProgramProfessionalEntity pp
       JOIN pp.programLinks link
       WHERE pp.deletedAt IS NULL
         AND COALESCE(pp.active, true) = true
         AND link.deletedAt IS NULL
         AND link.program.id = :programId
       ORDER BY pp.name ASC
    """)
    List<ProgramProfessionalEntity> findActiveByProgramId(@Param("programId") Integer programId);

    @Query("""
       SELECT DISTINCT pp FROM ProgramProfessionalEntity pp
       LEFT JOIN pp.programLinks link
       WHERE pp.deletedAt IS NULL
         AND (:q IS NULL OR TRIM(:q) = ''
              OR LOWER(pp.name) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(pp.email) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(pp.phone) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(pp.observation) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(pp.profession.name) LIKE LOWER(CONCAT('%', :q, '%')))
         AND (:professionId IS NULL OR pp.profession.id = :professionId)
         AND (:programId IS NULL OR link.program.id = :programId)
    """)
    Page<ProgramProfessionalEntity> search(@Param("q") String q,
                                           @Param("professionId") Integer professionId,
                                           @Param("programId") Integer programId,
                                           Pageable pageable);
}
