package com.cosam.project01.demand.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogMaintainerDTO {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private Boolean active;

    // Para cities
    private Integer regionId;
    private String regionCode;
    private String regionName;

    // Para semaphore_rules
    private String colorCode;
    private Integer minDays;
    private Integer maxDays;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
