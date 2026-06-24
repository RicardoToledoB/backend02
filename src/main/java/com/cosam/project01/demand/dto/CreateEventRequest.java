package com.cosam.project01.demand.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    private Integer stageId;
    private Integer eventTypeId;
    private String eventTypeCode;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private Integer attendanceStatusId;
    private String attendanceStatusCode;
    private String professionName;
    private Integer professionalUserId;
    private Integer programId;
    private String comment;
    private String citationComment;
    private String observation;
    private String nextAction;
    private LocalDate nextActionDate;
    private String resultCode;
    private String stateCode;
}
