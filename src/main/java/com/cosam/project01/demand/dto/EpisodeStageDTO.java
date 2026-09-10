package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeStageDTO {
    private Integer id;
    private Integer stageOrder;
    private ProgramSummaryDTO program;
    private Integer originStageId;
    /** Fecha funcional de ingreso a la etapa/programa. */
    private LocalDateTime receivedAt;
    /** Fecha real de registro de la etapa en el sistema. */
    private LocalDateTime createdAt;
    /** Fecha real de última actualización de la etapa en el sistema. */
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    private String stateCode;
    private String resultCode;
    private OptionDTO closureReason;
    private String closureComment;
    private Boolean current;
    private UserSummaryDTO responsibleUser;
    private Integer daysInStage;
}
