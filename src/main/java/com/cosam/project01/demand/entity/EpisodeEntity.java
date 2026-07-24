package com.cosam.project01.demand.entity;

import com.cosam.project01.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "episodes",
        indexes = {
                @Index(name = "idx_episode_postulant_active", columnList = "postulant_id, active"),
                @Index(name = "idx_episode_original_request_date", columnList = "original_request_date"),
                @Index(name = "idx_episode_current_program", columnList = "current_program_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE episodes SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EpisodeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 30)
    private String episodeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postulant_id", nullable = false)
    private PostulantEntity postulant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_type_id")
    private EpisodeTypeEntity episodeType;

    @Builder.Default
    @Column(name = "previous_treatment_number", nullable = false)
    private Integer previousTreatmentNumber = 0;

    @Column(nullable = false)
    private LocalDate originalRequestDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initial_program_id", nullable = false)
    private ProgramEntity initialProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_program_id")
    private ProgramEntity currentProgram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_stage_id")
    private EpisodeStageEntity currentStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_type_id")
    private ContactTypeEntity contactType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private SenderEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diverter_id")
    private DiverterEntity diverter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private ContactEntity contact;

    @Column(length = 60)
    private String stateCode;

    @Column(length = 80)
    private String resultCode;

    private LocalDateTime entryToTreatmentAt;
    private LocalDateTime egressAt;
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closure_reason_id")
    private ClosureReasonEntity closureReason;

    @Column(length = 1200)
    private String closureComment;

    private Boolean active;
    private Boolean waitingStopped;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private UserEntity createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_user_id")
    private UserEntity closedByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversed_by_user_id")
    private UserEntity reversedByUser;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.active == null) this.active = true;
        if (this.waitingStopped == null) this.waitingStopped = false;
        if (this.previousTreatmentNumber == null || this.previousTreatmentNumber < 0) this.previousTreatmentNumber = 0;
        if (this.stateCode == null) this.stateCode = "EN_TRAMITE";
        if (this.resultCode == null) this.resultCode = "AUN_SIN_RESULTADO";
        if (this.originalRequestDate == null) this.originalRequestDate = LocalDate.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.previousTreatmentNumber == null || this.previousTreatmentNumber < 0) this.previousTreatmentNumber = 0;
    }
}
