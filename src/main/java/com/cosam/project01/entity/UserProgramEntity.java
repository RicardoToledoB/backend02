package com.cosam.project01.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name="users_programs")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@SQLDelete(sql = "UPDATE users_programs SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class UserProgramEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="program_id")
    private ProgramEntity program;

    private Boolean isActive;
    private Boolean isSupervisor;
    private Boolean canReceiveReferences;
    private Boolean canManageCommunications;
    private Boolean canReceiveCitations;
    private Boolean canReceiveAttendances;
    private Boolean canReceiveFeedback;
    private Boolean canReceiveClosures;
    private Boolean canReceiveDocuments;
    private Boolean canReceiveObservations;
    private Boolean canManageDemands;
    private Boolean canViewDashboard;
    private String roleInProgram;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    private void createdAt(){
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
        if (this.isSupervisor == null) this.isSupervisor = false;
        applyBooleanDefaults();
    }

    @PreUpdate
    private void updatedAt(){
        this.updatedAt = LocalDateTime.now();
        applyBooleanDefaults();
    }

    private void applyBooleanDefaults() {
        if (this.isActive == null) this.isActive = true;
        if (this.isSupervisor == null) this.isSupervisor = false;
        if (this.canReceiveReferences == null) this.canReceiveReferences = false;
        if (this.canManageCommunications == null) this.canManageCommunications = false;
        if (this.canReceiveCitations == null) this.canReceiveCitations = false;
        if (this.canReceiveAttendances == null) this.canReceiveAttendances = false;
        if (this.canReceiveFeedback == null) this.canReceiveFeedback = false;
        if (this.canReceiveClosures == null) this.canReceiveClosures = false;
        if (this.canReceiveDocuments == null) this.canReceiveDocuments = false;
        if (this.canReceiveObservations == null) this.canReceiveObservations = false;
        if (this.canManageDemands == null) this.canManageDemands = false;
        if (this.canViewDashboard == null) this.canViewDashboard = false;
    }
}
