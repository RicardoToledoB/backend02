package com.cosam.project01.repository;

import com.cosam.project01.entity.ProgramEntity;
import com.cosam.project01.entity.UserProgramEntity;
import com.cosam.project01.entity.UserRoleEntity;
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
public interface UserProgramRepository extends JpaRepository<UserProgramEntity, Integer> {

    @Query(
            value = "SELECT * FROM users_programs c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<UserProgramEntity> findAllDeleted();

    @Query("SELECT ur FROM UserProgramEntity ur WHERE ur.deletedAt IS NULL")
    List<UserProgramEntity> findAllActive();

    @Query(value = "SELECT * FROM users_programs c WHERE c.id = :id", nativeQuery = true)
    Optional<UserProgramEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM users_programs", nativeQuery = true)
    List<UserProgramEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM UserProgramEntity c")
    Page<UserProgramEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM UserProgramEntity c
       WHERE (:id IS NULL OR c.id = :id)
    """)
    Page<UserProgramEntity> search(@Param("id") Integer id, Pageable pageable);



    // Busca todos los UserRoleEntity por el ID del usuario
    List<UserProgramEntity> findByUserId(Integer userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserProgramEntity up WHERE up.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);


    @Query("SELECT up FROM UserProgramEntity up WHERE up.user.id = :userId AND up.program.id = :programId AND up.deletedAt IS NULL")
    Optional<UserProgramEntity> findByUserIdAndProgramId(@Param("userId") Integer userId, @Param("programId") Integer programId);

    @Transactional
    @Modifying
    @Query("UPDATE UserProgramEntity up SET up.deletedAt = CURRENT_TIMESTAMP WHERE up.user.id = :userId AND up.program.id = :programId")
    void softDeleteByUserAndProgram(@Param("userId") Integer userId, @Param("programId") Integer programId);



    @Query("""
   SELECT p.name
   FROM UserProgramEntity up
   JOIN up.program p
   WHERE up.user.id = :userId AND up.deletedAt IS NULL
""")
    List<String> findProgramNamesByUserId(@Param("userId") Integer userId);

    @Query("""
   SELECT p.id
   FROM UserProgramEntity up
   JOIN up.program p
   WHERE up.user.id = :userId AND up.deletedAt IS NULL
""")
    List<Integer> findProgramIdsByUserId(@Param("userId") Integer userId);


    @Query("""
   SELECT up.program
   FROM UserProgramEntity up
   WHERE up.user.id = :userId AND up.deletedAt IS NULL
""")
    List<ProgramEntity> findProgramsByUserIdFull(@Param("userId") Integer userId);


}
