package com.cosam.project01.repository;

import com.cosam.project01.entity.RoleEntity;
import com.cosam.project01.entity.RoleEntity;
import jdk.jfr.Registered;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Integer> {


    @Query(
            value = "SELECT * FROM roles c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<RoleEntity> findAllDeleted();

    @Query("SELECT ur FROM RoleEntity ur WHERE ur.deletedAt IS NULL")
    List<RoleEntity> findAllActive();

    @Query(value = "SELECT * FROM roles c WHERE c.id = :id", nativeQuery = true)
    Optional<RoleEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM roles", nativeQuery = true)
    List<RoleEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM RoleEntity c")
    Page<RoleEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM RoleEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<RoleEntity> search(@Param("name") String name, Pageable pageable);
}
