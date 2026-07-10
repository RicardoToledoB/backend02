package com.cosam.project01.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramProfessionalProgramRelationDTO {
    private Long id;
    private Long programProfessionalId;
    private Integer programId;
    private String programName;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
