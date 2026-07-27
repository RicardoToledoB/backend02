package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseEpisodeRequest {
    private Integer closureReasonId;
    private String closureReasonCode;
    private String closureComment;

    @Schema(description = "Fecha y hora efectiva de cierre. Si no se informa, el backend usa la fecha/hora actual.", example = "2026-07-27T10:30:00")
    private LocalDateTime closureDate;

    private Boolean confirmImpact;
}
