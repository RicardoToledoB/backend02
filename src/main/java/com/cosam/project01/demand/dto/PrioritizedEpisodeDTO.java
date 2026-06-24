package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrioritizedEpisodeDTO {
    private Integer episodeId;
    private String episodeCode;
    private String rut;
    private String personName;
    private ProgramSummaryDTO currentProgram;
    private LocalDate originalRequestDate;
    private Integer accumulatedDays;
    private String semaphoreColor;
    private String stateCode;
    private String resultCode;
    private String lastManagement;
    private String suggestedAction;
}
