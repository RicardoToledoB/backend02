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
public class ContactDTO {

    private Integer id;
    private String name;
    private String description;
    private String cellphone;
    private String email;

    /**
     * Campo simple recomendado para frontend.
     * Permite crear/editar un referente enviando { "postulantId": 123 }.
     */
    private Integer postulantId;

    /**
     * Se mantiene compatibilidad con payloads antiguos que envían postulant: { id: ... }.
     */
    private PostulantDTO postulant;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
