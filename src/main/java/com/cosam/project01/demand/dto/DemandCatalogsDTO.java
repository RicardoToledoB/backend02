package com.cosam.project01.demand.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandCatalogsDTO {
    private List<OptionDTO> episodeTypes;
    private List<OptionDTO> eventTypes;
    private List<OptionDTO> attendanceStatuses;
    private List<OptionDTO> closureReasons;
    private List<OptionDTO> programPopulations;
    private List<OptionDTO> programModalities;
    private List<OptionDTO> programPlans;
    private List<OptionDTO> documentTypes;
    private List<OptionDTO> alertTypes;
    private List<OptionDTO> priorityLevels;
    private List<OptionDTO> alertStatuses;
    private List<OptionDTO> regions;
    private List<OptionDTO> cities;
}
