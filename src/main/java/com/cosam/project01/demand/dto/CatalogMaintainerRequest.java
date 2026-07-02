package com.cosam.project01.demand.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogMaintainerRequest {
    private String code;
    private String name;
    private String description;
    private Boolean active;

    // Para cities
    private Integer regionId;

    // Para semaphore_rules
    private String colorCode;
    private Integer minDays;
    private Integer maxDays;
}
