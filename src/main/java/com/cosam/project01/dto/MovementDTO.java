package com.cosam.project01.dto;

import com.cosam.project01.entity.PostulantEntity;
import com.cosam.project01.entity.ProgramEntity;
import com.cosam.project01.entity.RegisterEntity;
import jakarta.persistence.Column;
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
public class MovementDTO {

    private Integer id;
    private PostulantDTO postulant;
    private RegisterDTO register;
    private ProgramDTO program_origin;
    private ProgramDTO program_destination;
    private String entry_date;
    private String exit_date;
    private String waiting_days;
    private String movement_type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

}
