package com.cosam.project01.demand.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEpisodeRequest {
    @NotNull
    private Integer postulantId;
    private Integer episodeTypeId;
    private String episodeTypeCode;
    private LocalDate originalRequestDate;
    @NotNull
    private Integer initialProgramId;
    private Integer responsibleUserId;
    private Integer contactTypeId;
    private Integer senderId;
    private Integer diverterId;
    private Integer contactId;
    private String initialObservation;
}
