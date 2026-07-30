package com.cosam.project01.demand.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EpisodePurgeResponse {
    private Integer episodeId;
    private String episodeCode;
    private Integer postulantId;
    private boolean databasePurged;
    private Map<String, Integer> deletedRows;
    private Integer deletedFiles;
    private Integer failedFiles;
    private List<String> failedFilePaths;
    private List<String> skippedUnsafeFilePaths;
}
