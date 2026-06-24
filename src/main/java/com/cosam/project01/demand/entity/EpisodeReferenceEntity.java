package com.cosam.project01.demand.entity;

import com.cosam.project01.entity.ProgramEntity;
import com.cosam.project01.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "episode_references")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE episode_references SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EpisodeReferenceEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private EpisodeEntity episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_stage_id", nullable = false)
    private EpisodeStageEntity originStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_stage_id", nullable = false)
    private EpisodeStageEntity destinationStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_program_id", nullable = false)
    private ProgramEntity originProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_program_id", nullable = false)
    private ProgramEntity destinationProgram;

    private LocalDateTime referenceDate;

    @Column(length = 500)
    private String reason;

    @Column(length = 2000)
    private String observation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private EpisodeDocumentEntity document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private UserEntity createdByUser;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.referenceDate == null) this.referenceDate = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
