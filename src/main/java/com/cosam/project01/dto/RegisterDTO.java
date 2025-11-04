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
public class RegisterDTO {


    private Integer id;


    private PostulantDTO postulant;


    private ContactTypeDTO contactType;


    private SenderDTO sender;


    private DiverterDTO diverter;


    private ProgramDTO program;


    private UserDTO user;

    private NotRelevantDTO notRelevant;

    private String date_attention;

    private String description;

    private String state;
    private String is_history;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
