package com.cosam.project01.demand.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubstanceRequest {
    private Integer substanceId;
    private String level;
    private Boolean primarySubstance;
    private Integer useOrder;
    private String observation;
}
