package com.cosam.project01.demand.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministrativeCorrectionResponse {
    private Integer episodeId;
    private String episodeCode;
    private Integer programId;
    private String programName;
    private Integer stageId;
    private String correctionReason;
    private String performedBy;
    private LocalDateTime performedAt;

    @Builder.Default
    private Map<String, Integer> counters = new LinkedHashMap<>();

    private Boolean episodeUpdated;
    private Boolean closureUpdated;
    private Integer auditRecords;
}
