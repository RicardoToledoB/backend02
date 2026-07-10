package com.cosam.project01.demand.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCitationRequest {
    private Integer stageId;
    @NotNull
    private LocalDate citationDate;
    @NotNull
    private LocalTime citationTime;
    private Integer professionalUserId;
    private Long programProfessionalId;
    private String professionName;
    private Integer programId;
    private String citationComment;
    private String nextAction;
    private LocalDate nextActionDate;
}
