package com.cosam.project01.demand.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostulantSummaryDTO {
    private Integer id;
    private String rut;
    private String firstName;
    private String firstLastName;
    private String secondLastName;
    private String birthdate;
    private String email;
    private String phone;
    private String address;
}
