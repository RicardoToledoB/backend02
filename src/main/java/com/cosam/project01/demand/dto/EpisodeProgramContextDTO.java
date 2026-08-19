package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeProgramContextDTO {

    @Schema(description = "ID del episodio consultado.", example = "13")
    private Integer episodeId;

    @Schema(description = "ID del programa solicitado.", example = "2")
    private Integer programId;

    @Schema(description = "Nombre del programa solicitado.", example = "PAB THOMAS FENTON")
    private String programName;

    @Schema(description = "ID de la última etapa encontrada para el programa dentro del episodio. Retorna null si el programa no tiene etapa en ese episodio.", example = "11")
    private Integer stageId;

    @Schema(description = "Estado de la etapa del programa consultado.", example = "EN_TRAMITE")
    private String stageStateCode;

    @Schema(description = "Resultado de la etapa del programa consultado.", example = "AUN_SIN_RESULTADO")
    private String stageResultCode;

    @Schema(description = "Fecha y hora de recepción de la etapa del programa consultado.")
    private LocalDateTime receivedAt;

    @Schema(description = "Fecha y hora de cierre de la etapa del programa consultado. Retorna null si está abierta.")
    private LocalDateTime closureDate;

    @Schema(description = "Indica si la etapa del programa consultado está cerrada.", example = "false")
    private Boolean closed;
}
