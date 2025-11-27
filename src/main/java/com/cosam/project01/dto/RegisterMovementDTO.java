package com.cosam.project01.dto;
import com.cosam.project01.entity.ProfessionEntity;
import com.cosam.project01.entity.RegisterEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMovementDTO {

    private Integer id;
    private RegisterDTO register;
    private ProfessionDTO profession;
    private String full_name;
    private String date_attention;
    private String hour_attention;

    private String state;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;


}
