package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeProgramContextRequest {

    @NotNull
    @Schema(description = "ID del programa sobre el cual se requiere consultar el contexto dentro de los episodios.", example = "2")
    private Integer programId;

    @NotEmpty
    @Schema(description = "Lista de episodios para los cuales se requiere obtener la etapa correspondiente al programa consultado.", example = "[13, 12, 9, 11]")
    private List<Integer> episodeIds;
}
