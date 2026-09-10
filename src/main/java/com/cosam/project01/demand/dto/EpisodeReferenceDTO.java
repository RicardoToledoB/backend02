package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeReferenceDTO {
    private Integer id;
    private Integer episodeId;
    private Integer originStageId;
    private Integer destinationStageId;
    private ProgramSummaryDTO originProgram;
    private ProgramSummaryDTO destinationProgram;
    /** Fecha funcional de referencia/ingreso al programa destino. */
    private LocalDateTime referenceDate;

    /** Fecha real de registro/auditoría de la operación en el sistema. */
    private LocalDateTime createdAt;

    /** Fecha real de última actualización/auditoría de la referencia. */
    private LocalDateTime updatedAt;

    private String reason;
    private String observation;
    private UserSummaryDTO createdByUser;
}
