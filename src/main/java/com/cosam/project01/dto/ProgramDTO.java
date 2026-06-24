package com.cosam.project01.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDTO {


    private Integer id;
    private String name;
    private Integer populationTypeId;
    private Integer modalityId;
    private Integer planId;
    private Integer regionId;
    private Integer cityId;
    private String address;
    private String phone;
    private String email;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
