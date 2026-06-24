package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeDTO {
    private Integer id;
    private String episodeCode;
    private PostulantSummaryDTO postulant;
    private OptionDTO episodeType;
    private LocalDate originalRequestDate;
    private ProgramSummaryDTO initialProgram;
    private ProgramSummaryDTO currentProgram;
    private Integer currentStageId;
    private String stateCode;
    private String resultCode;
    private LocalDateTime entryToTreatmentAt;
    private LocalDateTime egressAt;
    private LocalDateTime closedAt;
    private OptionDTO closureReason;
    private String closureComment;
    private Boolean active;
    private Boolean waitingStopped;
    private Integer accumulatedDays;
    private String semaphoreColor;
    private UserSummaryDTO createdByUser;
    private UserSummaryDTO closedByUser;
    private UserSummaryDTO reversedByUser;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
