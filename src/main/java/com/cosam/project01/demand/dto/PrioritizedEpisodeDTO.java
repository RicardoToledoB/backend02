package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrioritizedEpisodeDTO {
    private Integer episodeId;
    private String episodeCode;
    private String rut;
    private String personName;
    private UserSummaryDTO createdByUser;
    private ProgramSummaryDTO currentProgram;
    private Integer currentStageId;
    private String currentStageStateCode;
    private String currentStageResultCode;
    private LocalDateTime currentStageReceivedAt;
    private Integer currentStageDays;
    private Integer originProgramId;
    private String originProgramName;
    private Long referenceCount;
    private LocalDate originalRequestDate;
    private Integer accumulatedDays;
    private String semaphoreColor;
    private String stateCode;
    private String resultCode;
    private String lastManagement;
    private LocalDate lastManagementDate;
    private LocalTime lastManagementTime;

    private LocalDate firstCitationFirstInterviewDate;
    private LocalDate secondCitationFirstInterviewDate;
    private LocalDate firstCitationSecondInterviewDate;
    private LocalDate secondCitationSecondInterviewDate;
    private LocalDate firstCitationThirdInterviewDate;
    private LocalDate secondCitationThirdInterviewDate;
    private LocalDate optionalInterviewDate;
    private LocalDate feedbackDate;
    private LocalDate closureDate;
    private String biopsychosocialCommitmentCode;
    private String feedbackResultCode;

    private String suggestedAction;
}
