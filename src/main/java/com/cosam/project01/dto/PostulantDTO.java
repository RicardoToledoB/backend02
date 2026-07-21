package com.cosam.project01.dto;
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

    /**
     * Convenio previsional asociado al postulante.
     * GET /api/v1/postulants/{id} retorna convPrev con su intPrev anidado.
     */
    private ConvPrevDTO convPrev;

    /**
     * Campo simple recomendado para crear/editar desde frontend.
     * Permite enviar { "convPrevId": 1 } sin armar el objeto completo.
     */
    private Integer convPrevId;

    private String firstName;
    private String lastName;
    private String firstLastName;
    private String secondLastName;
    private String rut;
    private String birthdate;
    private String email;
    private String phone;
    private String address;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
