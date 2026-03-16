package com.cosam.project01.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="movements")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@SQLDelete(sql = "UPDATE movements SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class MovementEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postulant_id")
    private PostulantEntity postulant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="register_id")
    private RegisterEntity register;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="program_origin_id")
    private ProgramEntity program_origin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="program_destination_id")
    private ProgramEntity program_destination;

    private String entry_date;
    private String exit_date;
    private String waiting_days;
    private String movement_type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    private void createdAt(){
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void updatedAt(){
        this.updatedAt = LocalDateTime.now();
    }


}