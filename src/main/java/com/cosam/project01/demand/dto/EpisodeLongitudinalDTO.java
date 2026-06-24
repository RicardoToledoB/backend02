package com.cosam.project01.demand.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeLongitudinalDTO {
    private PostulantSummaryDTO postulant;
    private EpisodeDTO activeEpisode;
    private List<EpisodeDTO> episodes;
    private List<EpisodeStageDTO> stages;
    private List<EpisodeEventDTO> events;
    private List<EpisodeReferenceDTO> references;
    private List<EpisodeAlertDTO> alerts;
    private List<EpisodeDocumentDTO> documents;
    private List<EpisodeAuditLogDTO> auditLogs;
}
