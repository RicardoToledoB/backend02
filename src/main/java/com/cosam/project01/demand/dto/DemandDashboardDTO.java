package com.cosam.project01.demand.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandDashboardDTO {
    private Long activeDemands;
    private Long waitingList;
    private Double averageAccumulatedDays;
    private Long redCases;
    private Long withoutFirstCitation;
    private Long openAlerts;
    private Map<String, Long> semaphoreDistribution;
    private List<PrioritizedEpisodeDTO> topCriticalCases;
}
