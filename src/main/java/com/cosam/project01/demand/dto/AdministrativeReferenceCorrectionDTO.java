package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Corrección administrativa de referencias entre programas. action: CREATE, UPDATE o DELETE.")
public class AdministrativeReferenceCorrectionDTO {
    @Schema(description = "CREATE, UPDATE o DELETE. Si no viene, se infiere por la presencia de id/referenceId.", example = "UPDATE")
    private String action;

    @Schema(description = "Identificador de la referencia. Alias de referenceId.", example = "5")
    private Integer id;

    @Schema(description = "Identificador de la referencia. Alias de id.", example = "5")
    private Integer referenceId;

    @Schema(description = "Etapa origen de la referencia. Si no viene, usa stageId/programId del request principal.", example = "35")
    private Integer originStageId;

    @Schema(description = "Etapa destino existente. Si no viene en CREATE, se crea una nueva etapa para destinationProgramId.", example = "36")
    private Integer destinationStageId;

    @Schema(description = "Programa destino de la referencia. Obligatorio en CREATE si no se informa destinationStageId.", example = "3")
    private Integer destinationProgramId;

    @Schema(description = "Fecha efectiva de referencia/ingreso al programa destino. Acepta YYYY-MM-DD o YYYY-MM-DDTHH:mm:ss.", example = "2026-09-01")
    private String referenceDate;

    @Schema(description = "Motivo de la referencia.", example = "Derivación a programa de mayor complejidad")
    private String reason;

    @Schema(description = "Observación administrativa de la referencia.")
    private String observation;

    @Schema(description = "Evento REFERENCIA asociado. Opcional; si no viene, el backend intenta resolverlo por episodio, etapa origen y fecha.", example = "120")
    private Integer eventId;

    @Schema(description = "Si es true, marca la etapa destino como etapa actual del episodio. Por defecto no altera la ubicación actual en corrección administrativa.", example = "false")
    private Boolean makeDestinationCurrent;
}
