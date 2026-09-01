package com.cosam.project01.demand.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceEpisodeRequest {
    private Integer originStageId;
    @NotNull
    private Integer destinationProgramId;
    private String referenceDate;
    private String reason;
    private String observation;
    private Boolean confirmImpact;
}
