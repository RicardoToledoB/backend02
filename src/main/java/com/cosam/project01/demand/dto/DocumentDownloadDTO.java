package com.cosam.project01.demand.dto;

import lombok.*;
import org.springframework.core.io.Resource;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDownloadDTO {
    private Resource resource;
    private String filename;
    private String mimeType;
    private Long fileSize;
}
