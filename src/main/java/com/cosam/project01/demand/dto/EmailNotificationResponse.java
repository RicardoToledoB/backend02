package com.cosam.project01.demand.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationResponse {
    private Boolean sent;
    private Boolean queued;
    private String result;
    private String message;
    private Integer episodeId;
    private Integer documentId;
    private String to;
    private String subject;
    private List<Integer> documentIds;
    private LocalDateTime sentAt;
}
