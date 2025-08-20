package com.cosam.project01.repository;

import com.cosam.project01.entity.ContactTypeEntity;
import com.cosam.project01.entity.ContactTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactTypeRepository extends JpaRepository<ContactTypeEntity,Integer> {

    @Query(
            value = "SELECT * FROM contacts_types c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<ContactTypeEntity> findAllDeleted();

    @Query("SELECT ur FROM ContactTypeEntity ur WHERE ur.deletedAt IS NULL")
    List<ContactTypeEntity> findAllActive();

    @Query(value = "SELECT * FROM contacts_types c WHERE c.id = :id", nativeQuery = true)
    Optional<ContactTypeEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM contacts_types", nativeQuery = true)
    List<ContactTypeEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM ContactTypeEntity c")
    Page<ContactTypeEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM ContactTypeEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<ContactTypeEntity> search(@Param("name") String name, Pageable pageable);

}
