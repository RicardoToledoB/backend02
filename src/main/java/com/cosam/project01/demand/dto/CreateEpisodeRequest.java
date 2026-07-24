package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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

    @Min(0)
    @Schema(description = "Número de tratamientos previos declarados para el episodio. Reemplaza funcionalmente a registers.number_tto del sistema anterior. Puede ser propuesto por frontend, pero debe poder ser corregido manualmente antes de guardar.", example = "0", defaultValue = "0", minimum = "0")
    private Integer previousTreatmentNumber;

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
