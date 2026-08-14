package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseEpisodeRequest {
    @Schema(description = "Identificador de la etapa concreta que se desea cerrar. Si no se informa, se usa la etapa actual para mantener compatibilidad.", example = "11")
    private Integer stageId;

    private Integer closureReasonId;
    private String closureReasonCode;
    private String closureComment;
    private String observation;
    private String comment;

    @Schema(description = "Fecha efectiva de cierre. Acepta fecha YYYY-MM-DD o fecha/hora ISO YYYY-MM-DDTHH:mm:ss. Si no se informa, el backend usa la fecha/hora actual.", example = "2026-07-27")
    private String closureDate;

    private Boolean confirmImpact;
}
