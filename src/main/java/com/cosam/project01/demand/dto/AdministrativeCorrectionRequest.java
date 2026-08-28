package com.cosam.project01.demand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de corrección administrativa transaccional de un episodio. Permite corregir datos del episodio, sustancias, eventos y cierre de una etapa/programa específico.")
public class AdministrativeCorrectionRequest {

    @Schema(description = "Programa sobre el cual se realiza la corrección. Si no se informa stageId, se buscará la última etapa del programa dentro del episodio.", example = "2")
    private Integer programId;

    @Schema(description = "Etapa concreta sobre la cual se realiza la corrección. Tiene prioridad sobre programId.", example = "11")
    private Integer stageId;

    @NotBlank
    @Schema(description = "Motivo obligatorio de la corrección administrativa. Se registra en auditoría.", example = "Corrección administrativa solicitada por supervisión tras revisión de antecedentes.")
    private String correctionReason;

    @Schema(description = "Datos generales del episodio a corregir. Solo se actualizan los campos enviados.")
    private AdministrativeEpisodeCorrectionDTO episode;

    @Schema(description = "Datos de cierre de la etapa/programa corregido. Por defecto cierra o actualiza solo la etapa indicada, no el episodio completo.")
    private AdministrativeStageClosureCorrectionDTO closure;

    @Builder.Default
    @Schema(description = "Correcciones de sustancias asociadas al episodio. action puede ser CREATE, UPDATE o DELETE.")
    private List<AdministrativeSubstanceCorrectionDTO> substances = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Correcciones genéricas de eventos. action puede ser CREATE, UPDATE o DELETE.")
    private List<AdministrativeEventCorrectionDTO> events = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Atajo para corregir citaciones. Si eventTypeCode no viene informado, se usa CITACION.")
    private List<AdministrativeEventCorrectionDTO> citations = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Atajo para corregir asistencias. Si eventTypeCode no viene informado, se usa ASISTENCIA.")
    private List<AdministrativeEventCorrectionDTO> attendances = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Atajo para corregir retroalimentaciones. Si eventTypeCode no viene informado, se usa RETROALIMENTACION.")
    private List<AdministrativeEventCorrectionDTO> feedbacks = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Atajo para corregir observaciones. Si eventTypeCode no viene informado, se usa OBSERVACION.")
    private List<AdministrativeEventCorrectionDTO> observations = new ArrayList<>();
}
