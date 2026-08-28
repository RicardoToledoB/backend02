package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Corrección de cierre de una etapa/programa. Si closeEpisode=true, además cierra el episodio completo.")
public class AdministrativeStageClosureCorrectionDTO {
    private Integer stageId;
    private Integer closureReasonId;
    private String closureReasonCode;
    private String closureDate;
    private LocalDateTime closedAt;
    private String closureComment;
    private String observation;
    private String comment;
    private String stateCode;
    private String resultCode;
    private Boolean closed;
    private Boolean closeEpisode;
}
