package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.cosam.project01.demand.jackson.FlexibleLocalTimeDeserializer;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    private Integer stageId;
    private Integer relatedEventId;
    private Integer eventTypeId;
    private String eventTypeCode;
    private LocalDate eventDate;
    @JsonDeserialize(using = FlexibleLocalTimeDeserializer.class)
    private LocalTime eventTime;
    private Integer attendanceStatusId;
    private String attendanceStatusCode;
    private String professionName;
    private Integer professionalUserId;
    private Long programProfessionalId;
    private Integer programId;
    private String comment;
    private String citationComment;
    private String observation;
    private String nextAction;
    private LocalDate nextActionDate;
    private String resultCode;
    private String stateCode;

    @Schema(description = "Código del nivel de compromiso biopsicosocial. Obligatorio cuando eventTypeCode es RETROALIMENTACION. Se guarda en episode_events.biopsychosocial_commitment_level_id.", example = "MODERADO")
    private String biopsychosocialCommitmentCode;
}
