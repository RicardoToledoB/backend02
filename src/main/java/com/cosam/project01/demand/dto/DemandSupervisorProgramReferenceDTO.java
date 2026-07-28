package com.cosam.project01.demand.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandSupervisorProgramReferenceDTO {
    private Integer programId;
    private String programName;
    private Long receivedReferences;
    private Long sentReferences;
    private Long pendingReferences;
    private Long referenceBalance;
    private Double averageDaysBeforeReference;
    private List<ReferenceReasonDTO> referenceReasons;
}
