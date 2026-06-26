package com.cosam.project01.demand.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationRequest {
    private Integer episodeId;
    private Integer documentId;
    @NotBlank
    private String to;
    private String cc;
    private String bcc;
    @NotBlank
    private String subject;
    @NotBlank
    private String message;
    private List<Integer> documentIds;
}
