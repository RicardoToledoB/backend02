package com.cosam.project01.demand.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReverseEpisodeRequest {
    @NotBlank
    private String reason;
}
