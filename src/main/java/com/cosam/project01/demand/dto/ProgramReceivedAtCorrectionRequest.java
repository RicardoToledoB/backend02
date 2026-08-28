package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud administrativa para corregir exclusivamente la fecha de ingreso de un programa dentro de un episodio.")
public class ProgramReceivedAtCorrectionRequest {

    @Schema(description = "Opcional. Etapa concreta a corregir cuando el mismo programa participa más de una vez en el episodio. Si no se informa, se usa la última etapa del programa.", example = "35")
    private Integer stageId;

    @NotBlank
    @Schema(description = "Nueva fecha/hora de ingreso al programa. Acepta YYYY-MM-DD o YYYY-MM-DDTHH:mm:ss.", example = "2026-01-23")
    private String receivedAt;

    @NotBlank
    @Schema(description = "Motivo obligatorio de la corrección. Se registra en auditoría.", example = "Corrección de fecha de ingreso al programa")
    private String correctionReason;
}
