package com.cosam.project01.dto;

import com.cosam.project01.entity.IntPrevEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConvPrevDTO {

    private Integer id;
    private String name;
    private IntPrevDTO intPrev;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
