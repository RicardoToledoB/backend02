package com.cosam.project01.demand.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseEpisodeRequest {
    private Integer closureReasonId;
    private String closureReasonCode;
    private String closureComment;
    private Boolean confirmImpact;
}
