package com.cosam.project01.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Integer id;
    private String firstName;
    private String secondName;
    private String firstLastName;
    private String secondLastName;
    private String email;
    private String username;
    private String password;
    private String rut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

   // private ProgramDTO program;
}
