package com.cosam.project01.repository;

import com.cosam.project01.entity.ProgramProfessionalProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramProfessionalProgramRepository extends JpaRepository<ProgramProfessionalProgramEntity, Long> {

    @Query(value = "SELECT * FROM program_professional_programs WHERE program_professional_id = :professionalId ORDER BY id", nativeQuery = true)
    List<ProgramProfessionalProgramEntity> findAllByProfessionalIncludingDeleted(@Param("professionalId") Long professionalId);

    @Query(value = """
            SELECT * FROM program_professional_programs
            WHERE program_professional_id = :professionalId
              AND program_id = :programId
            LIMIT 1
            """, nativeQuery = true)
    Optional<ProgramProfessionalProgramEntity> findAnyByProfessionalAndProgram(@Param("professionalId") Long professionalId,
                                                                               @Param("programId") Integer programId);

    @Query("""
       SELECT link FROM ProgramProfessionalProgramEntity link
       WHERE link.deletedAt IS NULL
         AND link.programProfessional.id = :professionalId
       ORDER BY link.program.name ASC
    """)
    List<ProgramProfessionalProgramEntity> findActiveByProfessionalId(@Param("professionalId") Long professionalId);

    @Modifying
    @Query("""
       UPDATE ProgramProfessionalProgramEntity link
       SET link.deletedAt = :deletedAt
       WHERE link.programProfessional.id = :professionalId
         AND link.deletedAt IS NULL
         AND link.program.id NOT IN :programIds
    """)
    void softDeleteMissingPrograms(@Param("professionalId") Long professionalId,
                                   @Param("programIds") List<Integer> programIds,
                                   @Param("deletedAt") LocalDateTime deletedAt);

    @Modifying
    @Query("""
       UPDATE ProgramProfessionalProgramEntity link
       SET link.deletedAt = :deletedAt
       WHERE link.programProfessional.id = :professionalId
         AND link.deletedAt IS NULL
    """)
    void softDeleteAllPrograms(@Param("professionalId") Long professionalId,
                               @Param("deletedAt") LocalDateTime deletedAt);
    @Modifying
    @Query("""
       UPDATE ProgramProfessionalProgramEntity link
       SET link.deletedAt = NULL
       WHERE link.programProfessional.id = :professionalId
    """)
    void restoreAllPrograms(@Param("professionalId") Long professionalId);

}
