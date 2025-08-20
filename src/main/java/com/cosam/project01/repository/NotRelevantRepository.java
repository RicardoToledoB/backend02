package com.cosam.project01.repository;

import com.cosam.project01.entity.CommuneEntity;
import com.cosam.project01.entity.NotRelevantEntity;
import com.cosam.project01.entity.NotRelevantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotRelevantRepository extends JpaRepository<NotRelevantEntity,Integer> {

    @Query(
            value = "SELECT * FROM not_relevants c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<NotRelevantEntity> findAllDeleted();

    @Query("SELECT ur FROM NotRelevantEntity ur WHERE ur.deletedAt IS NULL")
    List<NotRelevantEntity> findAllActive();

    @Query(value = "SELECT * FROM not_relevants c WHERE c.id = :id", nativeQuery = true)
    Optional<NotRelevantEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM not_relevants", nativeQuery = true)
    List<NotRelevantEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM NotRelevantEntity c")
    Page<NotRelevantEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM NotRelevantEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<NotRelevantEntity> search(@Param("name") String name, Pageable pageable);

}
