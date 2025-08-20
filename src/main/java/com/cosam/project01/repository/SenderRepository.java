package com.cosam.project01.repository;

import com.cosam.project01.entity.SenderEntity;
import com.cosam.project01.entity.SenderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SenderRepository extends JpaRepository<SenderEntity,Integer> {


    @Query(
            value = "SELECT * FROM senders c WHERE c.deleted_at IS NOT NULL",
            nativeQuery = true
    )
    List<SenderEntity> findAllDeleted();

    @Query("SELECT ur FROM SenderEntity ur WHERE ur.deletedAt IS NULL")
    List<SenderEntity> findAllActive();

    @Query(value = "SELECT * FROM senders c WHERE c.id = :id", nativeQuery = true)
    Optional<SenderEntity> findAnyById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM senders", nativeQuery = true)
    List<SenderEntity> findAllIncludingDeleted();

    @Query("SELECT c FROM SenderEntity c")
    Page<SenderEntity> findAllPaginated(Pageable pageable);

    @Query("""
       SELECT c FROM SenderEntity c
       WHERE (:name IS NULL OR TRIM(:name) = '' 
              OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<SenderEntity> search(@Param("name") String name, Pageable pageable);
}
