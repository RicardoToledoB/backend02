package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeEventDTO {
    private Integer id;
    private Integer episodeId;
    private Integer stageId;
    private OptionDTO eventType;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private OptionDTO attendanceStatus;
    private String professionName;
    private UserSummaryDTO professionalUser;
    private UserSummaryDTO registeredByUser;
    private ProgramSummaryDTO program;
    private String comment;
    private String citationComment;
    private String observation;
    private String nextAction;
    private LocalDate nextActionDate;
    private String resultCode;
    private String stateCode;
    private LocalDateTime createdAt;
}
