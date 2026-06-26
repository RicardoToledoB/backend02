package com.cosam.project01.demand.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDocumentRequest {
    private Integer stageId;
    private Integer eventId;
    private Integer referenceId;
    private String documentTypeCode;
    private String originalFilename;
}
