package com.cosam.project01.demand.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceReasonDTO {
    private String reason;
    private Long count;
}
