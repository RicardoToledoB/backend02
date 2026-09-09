package com.cosam.project01.demand.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrioritizedEpisodeStageDTO {
    private Integer episodeId;
    private String episodeCode;
    private String rut;
    private String personName;
    private UserSummaryDTO createdByUser;

    /** Programa/etapa actual global del episodio. */
    private ProgramSummaryDTO currentProgram;
    private Integer currentStageId;

    /** Programa/etapa representado por esta fila de la bandeja longitudinal. */
    private ProgramSummaryDTO program;
    private Integer programId;
    private String programName;
    private Integer stageId;
    private Integer stageOrder;
    private Integer originStageId;
    private LocalDateTime receivedAt;
    private LocalDateTime closedAt;
    private LocalDate closureDate;
    private Integer daysInStage;
    private String stageStateCode;
    private String stageResultCode;
    private Boolean closed;
    private Boolean current;
    private OptionDTO closureReason;
    private String closureComment;
    private UserSummaryDTO responsibleUser;

    /** Datos globales del episodio repetidos por fila para facilitar la bandeja. */
    private LocalDate originalRequestDate;
    private Integer accumulatedDays;
    private String semaphoreColor;

    /** Última gestión y marcadores operativos calculados solo con eventos de esta etapa. */
    private String lastManagement;
    private LocalDate lastManagementDate;
    private LocalTime lastManagementTime;
    private LocalDate firstCitationFirstInterviewDate;
    private LocalDate secondCitationFirstInterviewDate;
    private LocalDate firstCitationSecondInterviewDate;
    private LocalDate secondCitationSecondInterviewDate;
    private LocalDate firstCitationThirdInterviewDate;
    private LocalDate secondCitationThirdInterviewDate;
    private LocalDate optionalInterviewDate;
    private LocalDate feedbackDate;
    private String feedbackResultCode;
    private String biopsychosocialCommitmentCode;
    private String suggestedAction;

    /** Gestiones pertenecientes exclusivamente al stageId de esta fila. */
    private List<EpisodeEventDTO> events;
}
