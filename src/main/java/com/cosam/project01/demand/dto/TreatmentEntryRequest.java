package com.cosam.project01.demand.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentEntryRequest {
    private LocalDateTime entryToTreatmentAt;
    private String comment;
    private Boolean confirmImpact;
}
