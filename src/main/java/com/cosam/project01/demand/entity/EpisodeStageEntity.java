package com.cosam.project01.demand.entity;

import com.cosam.project01.entity.ProgramEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "episode_stages",
        indexes = {
                @Index(name = "idx_stage_episode", columnList = "episode_id"),
                @Index(name = "idx_stage_program_current", columnList = "program_id, is_current")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE episode_stages SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EpisodeStageEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private EpisodeEntity episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private ProgramEntity program;

    private Integer stageOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_stage_id")
    private EpisodeStageEntity originStage;

    private LocalDateTime receivedAt;
    private LocalDateTime closedAt;

    @Column(length = 60)
    private String stateCode;

    @Column(length = 80)
    private String resultCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closure_reason_id")
    private ClosureReasonEntity closureReason;

    @Column(length = 1200)
    private String closureComment;

    @Column(name = "is_current")
    private Boolean current;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.receivedAt == null) this.receivedAt = LocalDateTime.now();
        if (this.current == null) this.current = true;
        if (this.stateCode == null) this.stateCode = "EN_TRAMITE";
        if (this.resultCode == null) this.resultCode = "AUN_SIN_RESULTADO";
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
