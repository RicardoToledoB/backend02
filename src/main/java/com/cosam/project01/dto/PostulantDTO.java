package com.cosam.project01.dto;
import com.cosam.project01.entity.ConvPrevEntity;
import com.cosam.project01.entity.NotRelevantEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostulantDTO {

    private Integer id;

    private UserDTO user;

    private CommuneDTO commune;

    private SexDTO sex;

    private ConvPrevDTO convPrev;

    private String firstName;
    private String lastName;
    private String firstLastName;
    private String secondLastName;
    private String rut;
    private String birthdate;
    private String email;
    private String phone;
    private String address;


    private NotRelevantDTO notRelevant;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
