package com.cosam.project01.demand.seeder;

import com.cosam.project01.demand.entity.*;
import com.cosam.project01.demand.repository.*;
import com.cosam.project01.entity.ResultEntity;
import com.cosam.project01.entity.StateEntity;
import com.cosam.project01.repository.ResultRepository;
import com.cosam.project01.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DemandCatalogSeeder implements CommandLineRunner {

    private final EpisodeTypeRepository episodeTypeRepository;
    private final EventTypeRepository eventTypeRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;
    private final ClosureReasonRepository closureReasonRepository;
    private final ProgramPopulationRepository populationRepository;
    private final ProgramModalityRepository modalityRepository;
    private final ProgramPlanRepository planRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final SemaphoreRuleRepository semaphoreRuleRepository;
    private final StateRepository stateRepository;
    private final ResultRepository resultRepository;

    @Override
    public void run(String... args) {
        seedEpisodeTypes();
        seedEventTypes();
        seedAttendanceStatuses();
        seedClosureReasons();
        seedStatesAndResults();
        seedProgramCatalogs();
        seedDocumentTypes();
        seedTerritory();
        seedSemaphoreRules();
    }

    private void seedEpisodeTypes() {
        saveEpisodeType("PRIMERA_SOLICITUD", "Primera solicitud");
        saveEpisodeType("NUEVA_DEMANDA_POSTERIOR_A_EGRESO", "Nueva demanda posterior a egreso");
        saveEpisodeType("NUEVA_DEMANDA_POSTERIOR_A_CIERRE", "Nueva demanda posterior a cierre");
        saveEpisodeType("OTRO", "Otro tipo de episodio");
    }

    private void seedEventTypes() {
        saveEventType("CITACION", "Citación");
        saveEventType("ASISTENCIA", "Asistencia");
        saveEventType("ENTREVISTA", "Entrevista / evaluación");
        saveEventType("OBSERVACION", "Observación general");
        saveEventType("RETROALIMENTACION", "Retroalimentación");
        saveEventType("REFERENCIA", "Referencia entre programas");
        saveEventType("INGRESO_TRATAMIENTO", "Ingreso a tratamiento");
        saveEventType("EGRESO", "Egreso");
        saveEventType("CIERRE", "Cierre");
        saveEventType("ALERTA", "Alerta");
        saveEventType("SEGUIMIENTO", "Seguimiento");
        saveEventType("RECTIFICACION", "Rectificación");
        saveEventType("REVERSION", "Reversión");
    }

    private void seedAttendanceStatuses() {
        saveAttendanceStatus("AGENDADO", "Agendado");
        saveAttendanceStatus("SE_PRESENTO", "Se presentó");
        saveAttendanceStatus("NO_SE_PRESENTO", "No se presentó");
        saveAttendanceStatus("CANCELA_PROGRAMA", "Cancela programa");
        saveAttendanceStatus("REPROGRAMADA", "Reprogramada");
        saveAttendanceStatus("PENDIENTE", "Pendiente");
    }

    private void seedClosureReasons() {
        saveClosureReason("EGRESO", "Egreso");
        saveClosureReason("CIERRE_POR_INASISTENCIAS", "Cierre por inasistencias");
        saveClosureReason("ABANDONO", "Abandono");
        saveClosureReason("NO_ES_PERFIL", "No es perfil");
        saveClosureReason("NO_CORRESPONDE_JURISDICCION", "No corresponde por jurisdicción");
        saveClosureReason("NO_CORRESPONDE_PREVISION", "No corresponde por previsión");
        saveClosureReason("NO_CORRESPONDE_DIAGNOSTICO", "No corresponde por diagnóstico");
        saveClosureReason("NO_CORRESPONDE_OTROS", "No corresponde - otros");
        saveClosureReason("OTRO_CIERRE", "Otro cierre formal");
    }


    private void seedStatesAndResults() {
        saveState("EN_TRAMITE", "En trámite", "EPISODE", "Demanda o etapa activa en proceso de gestión.");
        saveState("CERRADO", "Cerrado", "EPISODE", "Episodio o etapa cerrada formalmente.");
        saveState("HISTORICO", "Histórico", "STAGE", "Etapa anterior cerrada por referencia o cierre.");
        saveState("INGRESO_TRATAMIENTO", "Ingreso a tratamiento", "EPISODE", "Ingreso efectivo a tratamiento; detiene KPI de espera.");
        saveState("EGRESADO", "Egresado", "EPISODE", "Episodio terminado por egreso.");

        saveResult("AUN_SIN_RESULTADO", "Aún sin resultado", "EPISODE", "Resultado inicial o pendiente de evaluación.");
        saveResult("LISTA_ESPERA", "Lista de espera", "EPISODE", "Mantiene conteo activo de espera.");
        saveResult("REFERENCIA", "Referencia", "EPISODE", "Referencia entre programas sin reiniciar días.");
        saveResult("INGRESO_TRATAMIENTO", "Ingreso a tratamiento", "EPISODE", "Detiene KPI de espera.");
        saveResult("EGRESO", "Egreso", "EPISODE", "Cierra el episodio por término del proceso.");
        saveResult("NO_ES_PERFIL", "No es perfil", "EPISODE", "Cierra episodio con aviso previo.");
        saveResult("NO_CORRESPONDE", "No corresponde", "EPISODE", "Cierra episodio exigiendo causal.");
        saveResult("ABANDONO", "Abandono", "EPISODE", "Cierra episodio por abandono formal.");
    }

    private void seedProgramCatalogs() {
        savePopulation("ADULTO", "Adulto");
        savePopulation("ADOLESCENTE", "Adolescente");

        saveModality("AMBULATORIO", "Ambulatorio");
        saveModality("RESIDENCIAL", "Residencial");

        savePlan("PLAN_GENERAL", "Plan general");
        savePlan("MUJERES", "Mujeres");
        savePlan("CALLE", "Personas en situación de calle");
        savePlan("SEMICERRADO", "Semicerrado");
        savePlan("CERRADO", "Cerrado");
        savePlan("OTRO", "Otro plan");
    }

    private void seedDocumentTypes() {
        saveDocumentType("CONSENTIMIENTO", "Consentimiento");
        saveDocumentType("INTERCONSULTA", "Interconsulta");
        saveDocumentType("INFORME_CLINICO", "Informe clínico");
        saveDocumentType("INFORME_SOCIAL", "Informe social");
        saveDocumentType("ORDEN_INGRESO", "Orden de ingreso");
        saveDocumentType("DOCUMENTO_EGRESO", "Documento de egreso");
        saveDocumentType("DOCUMENTO_CIERRE", "Documento de cierre");
        saveDocumentType("REFERENCIA", "Referencia");
        saveDocumentType("OTRO", "Otro documento");
    }

    private void seedTerritory() {
        RegionEntity magallanes = regionRepository.findByCodeIgnoreCase("MAGALLANES")
                .orElseGet(() -> {
                    RegionEntity r = new RegionEntity();
                    r.setCode("MAGALLANES");
                    r.setName("Región de Magallanes y de la Antártica Chilena");
                    r.setActive(true);
                    return regionRepository.save(r);
                });

        saveCity("PUNTA_ARENAS", "Punta Arenas", magallanes);
        saveCity("PUERTO_NATALES", "Puerto Natales", magallanes);
        saveCity("PORVENIR", "Porvenir", magallanes);
        saveCity("PUERTO_WILLIAMS", "Puerto Williams", magallanes);
    }

    private void seedSemaphoreRules() {
        saveSemaphore("VERDE", "Verde", 0, 30);
        saveSemaphore("AMARILLO", "Amarillo", 31, 60);
        saveSemaphore("ROJO", "Rojo", 61, null);
    }


    private void saveState(String code, String name, String scope, String description) {
        stateRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            StateEntity e = new StateEntity();
            e.setCode(code); e.setName(name); e.setScope(scope); e.setDescription(description); e.setActive(true);
            return stateRepository.save(e);
        });
    }

    private void saveResult(String code, String name, String scope, String description) {
        resultRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            ResultEntity e = new ResultEntity();
            e.setCode(code); e.setName(name); e.setScope(scope); e.setDescription(description); e.setActive(true);
            return resultRepository.save(e);
        });
    }

    private void saveEpisodeType(String code, String name) {
        episodeTypeRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            EpisodeTypeEntity e = new EpisodeTypeEntity();
            e.setCode(code); e.setName(name); e.setActive(true); return episodeTypeRepository.save(e);
        });
    }

    private void saveEventType(String code, String name) {
        eventTypeRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            EventTypeEntity e = new EventTypeEntity();
            e.setCode(code); e.setName(name); e.setActive(true); return eventTypeRepository.save(e);
        });
    }

    private void saveAttendanceStatus(String code, String name) {
        attendanceStatusRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            AttendanceStatusEntity e = new AttendanceStatusEntity();
            e.setCode(code); e.setName(name); e.setActive(true); return attendanceStatusRepository.save(e);
        });
    }

    private void saveClosureReason(String code, String name) {
        closureReasonRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            ClosureReasonEntity e = new ClosureReasonEntity();
            e.setCode(code); e.setName(name); e.setActive(true); return closureReasonRepository.save(e);
        });
    }

    private void savePopulation(String code, String name) {
        populationRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            ProgramPopulationEntity e = new ProgramPopulationEntity();
            e.setCode(code); e.setName(name); e.setActive(true); return populationRepository.save(e);
        });
    }

    private void saveModality(String code, String name) {
        modalityRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            ProgramModalityEntity e = new ProgramModalityEntity();
            e.setCode(code); e.setName(name); e.setActive(true); return modalityRepository.save(e);
        });
    }

    private void savePlan(String code, String name) {
        planRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            ProgramPlanEntity e = new ProgramPlanEntity();
            e.setCode(code); e.setName(name); e.setActive(true); return planRepository.save(e);
        });
    }

    private void saveDocumentType(String code, String name) {
        documentTypeRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            DocumentTypeEntity e = new DocumentTypeEntity();
            e.setCode(code); e.setName(name); e.setActive(true); return documentTypeRepository.save(e);
        });
    }

    private void saveCity(String code, String name, RegionEntity region) {
        cityRepository.findByCodeIgnoreCase(code).orElseGet(() -> {
            CityEntity e = new CityEntity();
            e.setCode(code); e.setName(name); e.setRegion(region); e.setActive(true); return cityRepository.save(e);
        });
    }

    private void saveSemaphore(String colorCode, String name, Integer minDays, Integer maxDays) {
        semaphoreRuleRepository.findByColorCodeIgnoreCase(colorCode).orElseGet(() -> {
            SemaphoreRuleEntity e = new SemaphoreRuleEntity();
            e.setColorCode(colorCode); e.setName(name); e.setMinDays(minDays); e.setMaxDays(maxDays); e.setActive(true); return semaphoreRuleRepository.save(e);
        });
    }
}
