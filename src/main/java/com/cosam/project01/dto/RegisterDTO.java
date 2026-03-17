package com.cosam.project01.dto;
import com.cosam.project01.entity.ContactEntity;
import com.cosam.project01.entity.ResultEntity;
import com.cosam.project01.entity.StateEntity;
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

    private ContactDTO contact;

    private ProgramDTO program;

    private String number_tto;

    private UserDTO user;

    private NotRelevantDTO notRelevant;

    private String date_attention;

    private String description;

    private ResultDTO result;

    private StateDTO state;
    private String is_history;

    private String date_close1;
    private String date_close2;
    private String date_close3;
    private String date_state1;
    private String date_state2;
    private String date_state3;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
