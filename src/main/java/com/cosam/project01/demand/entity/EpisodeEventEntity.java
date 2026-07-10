package com.cosam.project01.demand.entity;

import com.cosam.project01.entity.ProgramEntity;
import com.cosam.project01.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "episode_events",
        indexes = {
                @Index(name = "idx_event_episode_date", columnList = "episode_id, event_date"),
                @Index(name = "idx_event_stage", columnList = "stage_id"),
                @Index(name = "idx_event_related", columnList = "related_event_id"),
                @Index(name = "idx_event_professional", columnList = "professional_user_id"),
                @Index(name = "idx_event_program_professional", columnList = "program_professional_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE episode_events SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EpisodeEventEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private EpisodeEntity episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id")
    private EpisodeStageEntity stage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventTypeEntity eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_event_id")
    private EpisodeEventEntity relatedEvent;

    private LocalDate eventDate;
    private LocalTime eventTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_status_id")
    private AttendanceStatusEntity attendanceStatus;

    @Column(length = 120)
    private String professionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_user_id")
    private UserEntity professionalUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_professional_id")
    private ProgramProfessionalEntity programProfessional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_user_id")
    private UserEntity registeredByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private ProgramEntity program;

    @Column(length = 1200)
    private String comment;

    @Column(length = 1200)
    private String citationComment;

    @Column(length = 2000)
    private String observation;

    @Column(length = 500)
    private String nextAction;

    private LocalDate nextActionDate;

    @Column(length = 80)
    private String resultCode;

    @Column(length = 60)
    private String stateCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.eventDate == null) this.eventDate = LocalDate.now();
        if (this.eventTime == null) this.eventTime = LocalTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
