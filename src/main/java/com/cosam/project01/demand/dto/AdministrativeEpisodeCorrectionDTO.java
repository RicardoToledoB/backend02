package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Patch de datos generales del episodio. Los campos null no modifican el valor actual.")
public class AdministrativeEpisodeCorrectionDTO {
    private Integer episodeTypeId;
    private String episodeTypeCode;

    @Min(0)
    private Integer previousTreatmentNumber;

    private LocalDate originalRequestDate;
    private Integer initialProgramId;
    private Integer currentProgramId;
    private Integer currentStageId;
    private Integer contactTypeId;
    private Integer senderId;
    private Integer diverterId;
    private Integer contactId;
    private String stateCode;
    private String resultCode;
    private LocalDateTime entryToTreatmentAt;
    private LocalDateTime egressAt;
    private LocalDateTime closedAt;
    private Integer closureReasonId;
    private String closureReasonCode;
    private String closureComment;
    private Boolean active;
    private Boolean waitingStopped;
}
