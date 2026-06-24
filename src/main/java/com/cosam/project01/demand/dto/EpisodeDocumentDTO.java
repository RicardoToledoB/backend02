package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeDocumentDTO {
    private Integer id;
    private Integer episodeId;
    private Integer stageId;
    private Integer eventId;
    private Integer referenceId;
    private String documentTypeCode;
    private String originalFilename;
    private String storedFilename;
    private String storagePath;
    private String mimeType;
    private Long fileSize;
    private UserSummaryDTO uploadedByUser;
    private LocalDateTime uploadedAt;
}
