package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Código del tipo de citación seleccionado. Se guarda en episode_events.citation_type_id y se usa solo en eventos CITACION.", example = "PRIMERA_CITACION_PRIMERA_ENTREVISTA")
    private String citationTypeCode;

    private Integer professionalUserId;
    private Long programProfessionalId;
    private String professionName;
    private Integer programId;
    private String citationComment;
    private String nextAction;
    private LocalDate nextActionDate;
}
