package com.cosam.project01.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvPrevDTO {
    private Integer id;
    private String code;
    private String name;
    private String description;
    private Boolean active;

    private Integer intPrevId;
    private String intPrevCode;
    private String intPrevName;
    private IntPrevDTO intPrev;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
