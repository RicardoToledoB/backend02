package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeAlertDTO {
    private Integer id;
    private Integer episodeId;
    private Integer stageId;
    private String alertTypeCode;
    private String priorityLevelCode;
    private String description;
    private String actionTaken;
    private String nextAction;
    private LocalDate nextActionDate;
    private UserSummaryDTO responsibleUser;
    private String statusCode;
    private UserSummaryDTO createdByUser;
    private LocalDateTime createdAt;
}
