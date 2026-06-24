package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeReferenceDTO {
    private Integer id;
    private Integer episodeId;
    private Integer originStageId;
    private Integer destinationStageId;
    private ProgramSummaryDTO originProgram;
    private ProgramSummaryDTO destinationProgram;
    private LocalDateTime referenceDate;
    private String reason;
    private String observation;
    private UserSummaryDTO createdByUser;
}
