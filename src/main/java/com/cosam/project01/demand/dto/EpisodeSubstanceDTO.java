package com.cosam.project01.demand.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeSubstanceDTO {
    private Integer id;
    private Integer episodeId;
    private Integer substanceId;
    private String substanceName;
    private String level;
    private Boolean primarySubstance;
    private Integer useOrder;
    private String observation;
}
