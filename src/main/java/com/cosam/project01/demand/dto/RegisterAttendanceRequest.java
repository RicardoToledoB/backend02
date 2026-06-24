package com.cosam.project01.demand.dto;

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
    private LocalDate eventDate;
    private LocalTime eventTime;
    private Integer professionalUserId;
    private String professionName;
    @NotBlank
    private String attendanceStatusCode;
    private String comment;
    private String observation;
    private String resultCode;
    private String nextAction;
    private LocalDate nextActionDate;
}
