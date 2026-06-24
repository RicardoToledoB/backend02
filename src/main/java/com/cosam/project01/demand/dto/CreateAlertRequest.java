package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlertRequest {
    private Integer stageId;
    private String alertTypeCode;
    private String priorityLevelCode;
    private String description;
    private String actionTaken;
    private String nextAction;
    private LocalDate nextActionDate;
    private Integer responsibleUserId;
    private String statusCode;
}
