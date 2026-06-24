package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeAuditLogDTO {
    private Integer id;
    private Integer episodeId;
    private Integer stageId;
    private Integer eventId;
    private String actionType;
    private String previousValue;
    private String newValue;
    private String reason;
    private UserSummaryDTO performedByUser;
    private UserSummaryDTO authorizedByUser;
    private UserSummaryDTO reversedByUser;
    private LocalDateTime performedAt;
    private LocalDateTime reversedAt;
}
