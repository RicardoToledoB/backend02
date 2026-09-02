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
@Schema(description = "Corrección administrativa de un evento asociado al episodio. action: CREATE, UPDATE o DELETE.")
public class AdministrativeEventCorrectionDTO {
    private String action;
    private Integer id;
    private Integer eventId;
    private Integer stageId;
    private Integer relatedEventId;
    private Integer eventTypeId;
    private String eventTypeCode;
    private String citationTypeCode;
    private String biopsychosocialCommitmentCode;
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
}
