package com.cosam.project01.demand.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSummaryDTO {
    private Integer id;
    private String name;
    private OptionDTO populationType;
    private OptionDTO modality;
    private OptionDTO plan;
    private OptionDTO region;
    private OptionDTO city;
    private String address;
    private String phone;
    private String email;
    private Boolean active;
}
