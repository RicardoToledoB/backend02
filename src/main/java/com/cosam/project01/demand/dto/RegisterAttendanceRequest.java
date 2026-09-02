package com.cosam.project01.demand.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.cosam.project01.demand.jackson.FlexibleLocalTimeDeserializer;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAttendanceRequest {
    private Integer stageId;
    private Integer relatedEventId;
    private LocalDate eventDate;
    @JsonDeserialize(using = FlexibleLocalTimeDeserializer.class)
    private LocalTime eventTime;
    private Integer professionalUserId;
    private Long programProfessionalId;
    private String professionName;
    @NotBlank
    private String attendanceStatusCode;
    private String comment;
    private String observation;
    private String resultCode;
    private String nextAction;
    private LocalDate nextActionDate;
}
