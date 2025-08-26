package com.cosam.project01.repository;

import com.cosam.project01.entity.ContactEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity,Integer> {

    @Query(
            value = "SELECT * FROM contacts c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<ContactEntity> findAllDeleted();

    @Query("SELECT ur FROM ContactEntity ur WHERE ur.deletedAt IS NULL")
    List<ContactEntity> findAllActive();

    @Query(value = "SELECT * FROM contacts c WHERE c.id = :id", nativeQuery = true)
    Optional<ContactEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM contacts", nativeQuery = true)
    List<ContactEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM ContactEntity c")
    Page<ContactEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM ContactEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<ContactEntity> search(@Param("name") String name, Pageable pageable);

}
