package com.cosam.project01.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramProfessionalDTO {
    private Long id;
    private String name;

    private Integer professionId;
    private String professionCode;
    private String professionName;

    private String email;
    private String phone;
    private String observation;
    private Boolean active;

    private List<Integer> programIds;
    private List<ProgramDTO> programs;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
