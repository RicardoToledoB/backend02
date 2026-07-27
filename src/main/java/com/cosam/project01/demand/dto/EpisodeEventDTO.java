package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeEventDTO {
    private Integer id;
    private Integer episodeId;
    private Integer stageId;
    private Integer relatedEventId;
    private OptionDTO eventType;

    @Schema(description = "Tipo de citación asociado al evento. Solo se informa para eventos CITACION.")
    private OptionDTO citationType;

    @Schema(description = "Nivel de compromiso biopsicosocial asociado al evento. Solo se informa para eventos RETROALIMENTACION.")
    private OptionDTO biopsychosocialCommitmentLevel;

    private LocalDate eventDate;
    private LocalTime eventTime;
    private OptionDTO attendanceStatus;
    private String professionName;
    private UserSummaryDTO professionalUser;
    private Long programProfessionalId;
    private String programProfessionalName;
    private UserSummaryDTO registeredByUser;
    private ProgramSummaryDTO program;
    private String comment;
    private String citationComment;
    private String observation;
    private String nextAction;
    private LocalDate nextActionDate;
    private String resultCode;
    private String stateCode;
    private LocalDateTime createdAt;
}
