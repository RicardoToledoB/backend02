package com.cosam.project01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgramDTO {
    private Integer id;
    private Integer userId;
    private Integer programId;
    private UserDTO user;
    private ProgramDTO program;
    private Boolean transversal;
    private String communicationScope;
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
}
