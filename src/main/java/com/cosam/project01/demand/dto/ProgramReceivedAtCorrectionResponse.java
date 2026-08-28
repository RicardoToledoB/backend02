package com.cosam.project01.demand.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramReceivedAtCorrectionResponse {
    private Integer episodeId;
    private String episodeCode;
    private Integer programId;
    private String programName;
    private Integer stageId;
    private LocalDateTime previousReceivedAt;
    private LocalDateTime receivedAt;
    private Integer previousDaysInStage;
    private Integer daysInStage;
    private LocalDate previousOriginalRequestDate;
    private LocalDate originalRequestDate;
    private Boolean episodeOriginalRequestDateUpdated;
    private String correctionReason;
    private String performedBy;
    private LocalDateTime performedAt;
    private Integer auditRecords;
}
