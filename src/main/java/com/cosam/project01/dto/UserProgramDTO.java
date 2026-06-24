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
    private UserDTO user;
    private ProgramDTO program;
    private Boolean isActive;
    private Boolean isSupervisor;
    private Boolean canReceiveReferences;
    private Boolean canManageDemands;
    private Boolean canViewDashboard;
    private String roleInProgram;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
