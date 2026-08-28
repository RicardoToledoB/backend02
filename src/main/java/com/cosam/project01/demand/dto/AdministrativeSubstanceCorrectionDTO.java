package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Corrección administrativa de una sustancia asociada al episodio. action: CREATE, UPDATE o DELETE.")
public class AdministrativeSubstanceCorrectionDTO {
    private String action;
    private Integer id;
    private Integer substanceAssociationId;
    private Integer substanceId;
    private String level;
    private Boolean primarySubstance;
    private Integer useOrder;
    private String observation;
}
