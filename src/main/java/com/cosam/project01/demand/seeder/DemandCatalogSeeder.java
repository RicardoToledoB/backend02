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
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class DemandCatalogSeeder implements CommandLineRunner {

    private final EpisodeTypeRepository episodeTypeRepository;
    private final EventTypeRepository eventTypeRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;
    private final CitationTypeRepository citationTypeRepository;
    private final BiopsychosocialCommitmentLevelRepository biopsychosocialCommitmentLevelRepository;
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
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        seedEpisodeTypes();
        seedEventTypes();
        seedAttendanceStatuses();
        seedCitationTypes();
        seedBiopsychosocialCommitmentLevels();
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

    private void seedCitationTypes() {
        saveCitationType("PRIMERA_CITACION_PRIMERA_ENTREVISTA", "Primera citación a primera entrevista.", 1);
        saveCitationType("SEGUNDA_CITACION_PRIMERA_ENTREVISTA", "Segunda citación a primera entrevista.", 2);
        saveCitationType("PRIMERA_CITACION_SEGUNDA_ENTREVISTA", "Primera citación a segunda entrevista.", 3);
        saveCitationType("SEGUNDA_CITACION_SEGUNDA_ENTREVISTA", "Segunda citación a segunda entrevista.", 4);
        saveCitationType("ENTREVISTA_OPCIONAL", "Entrevista opcional.", 5);
    }

    private void seedBiopsychosocialCommitmentLevels() {
        saveBiopsychosocialCommitmentLevel("LEVE", "Leve");
        saveBiopsychosocialCommitmentLevel("MODERADO", "Moderado");
        saveBiopsychosocialCommitmentLevel("SEVERO", "Severo");
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
                    int updated = entityManager.createNativeQuery("""
                            UPDATE regions
                            SET name = :name,
                                active = true,
                                deleted_at = NULL
                            WHERE UPPER(code) = UPPER(:code)
                            """)
                            .setParameter("name", "Región de Magallanes y de la Antártica Chilena")
                            .setParameter("code", "MAGALLANES")
                            .executeUpdate();
                    if (updated > 0) {
                        return regionRepository.findByCodeIgnoreCase("MAGALLANES").orElseThrow();
                    }
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


    private boolean upsertBaseCatalog(String tableName, String code, String name) {
        int updated = entityManager.createNativeQuery("""
                UPDATE %s
                SET name = :name,
                    active = true,
                    deleted_at = NULL
                WHERE UPPER(code) = UPPER(:code)
                """.formatted(tableName))
                .setParameter("name", name)
                .setParameter("code", code)
                .executeUpdate();
        return updated > 0;
    }

    private void saveState(String code, String name, String scope, String description) {
        int updated = entityManager.createNativeQuery("""
                UPDATE states
                SET name = :name,
                    scope = :scope,
                    description = :description,
                    active = true,
                    deleted_at = NULL
                WHERE UPPER(code) = UPPER(:code)
                """)
                .setParameter("name", name)
                .setParameter("scope", scope)
                .setParameter("description", description)
                .setParameter("code", code)
                .executeUpdate();

        if (updated == 0) {
            StateEntity e = new StateEntity();
            e.setCode(code);
            e.setName(name);
            e.setScope(scope);
            e.setDescription(description);
            e.setActive(true);
            stateRepository.save(e);
        }
    }

    private void saveResult(String code, String name, String scope, String description) {
        int updated = entityManager.createNativeQuery("""
                UPDATE results
                SET name = :name,
                    scope = :scope,
                    description = :description,
                    active = true,
                    deleted_at = NULL
                WHERE UPPER(code) = UPPER(:code)
                """)
                .setParameter("name", name)
                .setParameter("scope", scope)
                .setParameter("description", description)
                .setParameter("code", code)
                .executeUpdate();

        if (updated == 0) {
            ResultEntity e = new ResultEntity();
            e.setCode(code);
            e.setName(name);
            e.setScope(scope);
            e.setDescription(description);
            e.setActive(true);
            resultRepository.save(e);
        }
    }

    private void saveEpisodeType(String code, String name) {
        if (!upsertBaseCatalog("episode_types", code, name)) {
            EpisodeTypeEntity e = new EpisodeTypeEntity();
            e.setCode(code); e.setName(name); e.setActive(true); episodeTypeRepository.save(e);
        }
    }

    private void saveEventType(String code, String name) {
        if (!upsertBaseCatalog("event_types", code, name)) {
            EventTypeEntity e = new EventTypeEntity();
            e.setCode(code); e.setName(name); e.setActive(true); eventTypeRepository.save(e);
        }
    }

    private void saveAttendanceStatus(String code, String name) {
        if (!upsertBaseCatalog("attendance_statuses", code, name)) {
            AttendanceStatusEntity e = new AttendanceStatusEntity();
            e.setCode(code); e.setName(name); e.setActive(true); attendanceStatusRepository.save(e);
        }
    }

    private void saveCitationType(String code, String name, Integer sortOrder) {
        CitationTypeEntity e = citationTypeRepository.findByCodeIgnoreCase(code).orElseGet(CitationTypeEntity::new);
        e.setCode(code);
        e.setName(name);
        e.setSortOrder(sortOrder);
        e.setActive(true);
        citationTypeRepository.save(e);
    }

    private void saveBiopsychosocialCommitmentLevel(String code, String name) {
        BiopsychosocialCommitmentLevelEntity e = biopsychosocialCommitmentLevelRepository.findByCodeIgnoreCase(code).orElseGet(BiopsychosocialCommitmentLevelEntity::new);
        e.setCode(code);
        e.setName(name);
        e.setActive(true);
        biopsychosocialCommitmentLevelRepository.save(e);
    }

    private void saveClosureReason(String code, String name) {
        if (!upsertBaseCatalog("closure_reasons", code, name)) {
            ClosureReasonEntity e = new ClosureReasonEntity();
            e.setCode(code); e.setName(name); e.setActive(true); closureReasonRepository.save(e);
        }
    }

    private void savePopulation(String code, String name) {
        if (!upsertBaseCatalog("program_populations", code, name)) {
            ProgramPopulationEntity e = new ProgramPopulationEntity();
            e.setCode(code); e.setName(name); e.setActive(true); populationRepository.save(e);
        }
    }

    private void saveModality(String code, String name) {
        if (!upsertBaseCatalog("program_modalities", code, name)) {
            ProgramModalityEntity e = new ProgramModalityEntity();
            e.setCode(code); e.setName(name); e.setActive(true); modalityRepository.save(e);
        }
    }

    private void savePlan(String code, String name) {
        if (!upsertBaseCatalog("program_plans", code, name)) {
            ProgramPlanEntity e = new ProgramPlanEntity();
            e.setCode(code); e.setName(name); e.setActive(true); planRepository.save(e);
        }
    }

    private void saveDocumentType(String code, String name) {
        if (!upsertBaseCatalog("document_types", code, name)) {
            DocumentTypeEntity e = new DocumentTypeEntity();
            e.setCode(code); e.setName(name); e.setActive(true); documentTypeRepository.save(e);
        }
    }

    private void saveCity(String code, String name, RegionEntity region) {
        int updated = entityManager.createNativeQuery("""
                UPDATE cities
                SET name = :name,
                    region_id = :regionId,
                    active = true,
                    deleted_at = NULL
                WHERE UPPER(code) = UPPER(:code)
                """)
                .setParameter("name", name)
                .setParameter("regionId", region.getId())
                .setParameter("code", code)
                .executeUpdate();

        if (updated == 0) {
            CityEntity e = new CityEntity();
            e.setCode(code); e.setName(name); e.setRegion(region); e.setActive(true); cityRepository.save(e);
        }
    }

    private void saveSemaphore(String colorCode, String name, Integer minDays, Integer maxDays) {
        int updated = entityManager.createNativeQuery("""
                UPDATE semaphore_rules
                SET name = :name,
                    min_days = :minDays,
                    max_days = :maxDays,
                    active = true,
                    deleted_at = NULL
                WHERE UPPER(color_code) = UPPER(:colorCode)
                """)
                .setParameter("name", name)
                .setParameter("minDays", minDays)
                .setParameter("maxDays", maxDays)
                .setParameter("colorCode", colorCode)
                .executeUpdate();

        if (updated == 0) {
            SemaphoreRuleEntity e = new SemaphoreRuleEntity();
            e.setColorCode(colorCode); e.setName(name); e.setMinDays(minDays); e.setMaxDays(maxDays); e.setActive(true); semaphoreRuleRepository.save(e);
        }
    }
}
