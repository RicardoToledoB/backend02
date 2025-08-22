package com.cosam.project01.repository;

import com.cosam.project01.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {


    @Query(
            value = "SELECT * FROM users c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<UserEntity> findAllDeleted();

    @Query("SELECT ur FROM UserEntity ur WHERE ur.deletedAt IS NULL")
    List<UserEntity> findAllActive();

    @Query(value = "SELECT * FROM users c WHERE c.id = :id", nativeQuery = true)
    Optional<UserEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<UserEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM UserEntity c")
    Page<UserEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM UserEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.username) LIKE LOWER(CONCAT('%', :username, '%')))
    """)
    Page<UserEntity> search(@Param("username") String username, Pageable pageable);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmailIgnoreCase(String email);

}
