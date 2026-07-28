package com.cosam.project01.demand.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandSupervisorProgramDTO {
    private Integer programId;
    private String programName;
    private Long activeDemands;
    private Double averageAccumulatedDays;
    private Long redCases;
    private Long withoutFirstCitation;
    private Long withoutFeedback;
    private Long severeCommitmentCases;
    private Long pendingReferences;
    private Long pendingClosures;
    private Long openAlerts;
}
