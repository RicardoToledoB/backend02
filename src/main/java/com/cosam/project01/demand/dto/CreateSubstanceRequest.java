package com.cosam.project01.demand.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubstanceRequest {
    @NotNull
    private Integer substanceId;
    private String level;
    private Boolean primarySubstance;
    private Integer useOrder;
    private String observation;
}
