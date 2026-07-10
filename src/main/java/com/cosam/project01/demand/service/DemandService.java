package com.cosam.project01.demand.service;

import com.cosam.project01.demand.dto.*;
import com.cosam.project01.demand.entity.*;
import com.cosam.project01.demand.repository.*;
import com.cosam.project01.entity.*;
import com.cosam.project01.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandService {

    private static final String STATE_IN_PROGRESS = "EN_TRAMITE";
    private static final String STATE_CLOSED = "CERRADO";
    private static final String RESULT_PENDING = "AUN_SIN_RESULTADO";
    private static final String RESULT_WAITING_LIST = "LISTA_ESPERA";
    private static final String RESULT_REFERENCE = "REFERENCIA";
    private static final String RESULT_TREATMENT_ENTRY = "INGRESO_TRATAMIENTO";
    private static final String RESULT_EGRESS = "EGRESO";
    private static final String RESULT_CLOSURE = "CIERRE";

    private final EpisodeRepository episodeRepository;
    private final EpisodeStageRepository stageRepository;
    private final EpisodeEventRepository eventRepository;
    private final EpisodeReferenceRepository referenceRepository;
    private final EpisodeDocumentRepository documentRepository;
    private final EpisodeAlertRepository alertRepository;
    private final EpisodeAuditLogRepository auditLogRepository;
    private final EpisodeSubstanceRepository episodeSubstanceRepository;

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

    private final PostulantRepository postulantRepository;
    private final ProgramRepository programRepository;
    private final ContactTypeRepository contactTypeRepository;
    private final SenderRepository senderRepository;
    private final DiverterRepository diverterRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final SubstanceRepository substanceRepository;

    @Value("${app.documents.storage-dir:./storage/demand-documents}")
    private String documentStorageDir;

    @Transactional(readOnly = true)
    public DemandCatalogsDTO getCatalogs() {
        return DemandCatalogsDTO.builder()
                .episodeTypes(episodeTypeRepository.findAll().stream().map(this::toOption).toList())
                .eventTypes(eventTypeRepository.findAll().stream().map(this::toOption).toList())
                .attendanceStatuses(attendanceStatusRepository.findAll().stream().map(this::toOption).toList())
                .closureReasons(closureReasonRepository.findAll().stream().map(this::toOption).toList())
                .programPopulations(populationRepository.findAll().stream().map(this::toOption).toList())
                .programModalities(modalityRepository.findAll().stream().map(this::toOption).toList())
                .programPlans(planRepository.findAll().stream().map(this::toOption).toList())
                .documentTypes(documentTypeRepository.findAll().stream().map(this::toOption).toList())
                .alertTypes(alertTypes())
                .priorityLevels(priorityLevels())
                .alertStatuses(alertStatuses())
                .regions(regionRepository.findAll().stream().map(this::toOption).toList())
                .cities(cityRepository.findAll().stream().map(this::toOption).toList())
                .build();
    }


    @Transactional(readOnly = true)
    public PostulantSummaryDTO getPersonByRut(String rut) {
        return postulantRepository.findFirstByRutNormalized(normalizeRut(rut))
                .map(this::toPostulantDTO)
                .orElseThrow(() -> notFound("Persona/postulante no encontrado"));
    }

    @Transactional
    public EpisodeDTO createEpisode(CreateEpisodeRequest request) {
        PostulantEntity postulant = postulantRepository.findById(request.getPostulantId())
                .orElseThrow(() -> notFound("Persona/postulante no encontrado"));

        Optional<EpisodeEntity> active = episodeRepository.findActiveByPostulantId(postulant.getId());
        if (active.isPresent()) {
            return toEpisodeDTO(active.get());
        }

        ProgramEntity program = programRepository.findById(request.getInitialProgramId())
                .orElseThrow(() -> notFound("Programa inicial no encontrado"));

        UserEntity currentUser = currentUserOrNull();
        UserEntity responsibleUser = request.getResponsibleUserId() != null
                ? userRepository.findById(request.getResponsibleUserId()).orElseThrow(() -> notFound("Responsable no encontrado"))
                : currentUser;
        EpisodeTypeEntity type = resolveEpisodeType(request.getEpisodeTypeId(), request.getEpisodeTypeCode(), "PRIMERA_SOLICITUD");

        EpisodeEntity episode = EpisodeEntity.builder()
                .postulant(postulant)
                .episodeType(type)
                .originalRequestDate(request.getOriginalRequestDate() != null ? request.getOriginalRequestDate() : LocalDate.now())
                .initialProgram(program)
                .currentProgram(program)
                .contactType(findNullable(contactTypeRepository, request.getContactTypeId()))
                .sender(findNullable(senderRepository, request.getSenderId()))
                .diverter(findNullable(diverterRepository, request.getDiverterId()))
                .contact(findNullable(contactRepository, request.getContactId()))
                .stateCode(STATE_IN_PROGRESS)
                .resultCode(RESULT_PENDING)
                .active(true)
                .waitingStopped(false)
                .createdByUser(currentUser)
                .build();

        episode = episodeRepository.save(episode);
        episode.setEpisodeCode(String.format("DEM-%06d", episode.getId()));
        episode = episodeRepository.save(episode);

        EpisodeStageEntity stage = EpisodeStageEntity.builder()
                .episode(episode)
                .program(program)
                .stageOrder(1)
                .receivedAt(LocalDateTime.now())
                .stateCode(STATE_IN_PROGRESS)
                .resultCode(RESULT_PENDING)
                .current(true)
                .responsibleUser(responsibleUser)
                .build();
        stage = stageRepository.save(stage);

        episode.setCurrentStage(stage);
        episode = episodeRepository.save(episode);

        audit(episode, stage, null, "CREAR_EPISODIO", null, "Episodio creado", request.getInitialObservation(), currentUser, null, null);

        if (hasText(request.getInitialObservation())) {
            createInternalEvent(episode, stage, "OBSERVACION", null, null, program, currentUser, currentUser,
                    "Observación inicial", null, request.getInitialObservation(), null, null, RESULT_PENDING, STATE_IN_PROGRESS);
        }

        return toEpisodeDTO(episode);
    }

    @Transactional(readOnly = true)
    public EpisodeDTO getEpisode(Integer id) {
        return toEpisodeDTO(findEpisode(id));
    }

    @Transactional(readOnly = true)
    public EpisodeDTO getActiveByRut(String rut) {
        return episodeRepository.findActiveByRut(normalizeRut(rut))
                .map(this::toEpisodeDTO)
                .orElseThrow(() -> notFound("La persona no tiene episodio activo"));
    }

    @Transactional(readOnly = true)
    public EpisodeLongitudinalDTO getLongitudinalByRut(String rut) {
        List<EpisodeEntity> episodes = episodeRepository.findHistoryByRut(normalizeRut(rut));
        if (episodes.isEmpty()) {
            throw notFound("No se encontró historia para el RUT indicado");
        }
        return buildLongitudinal(episodes);
    }

    @Transactional(readOnly = true)
    public EpisodeLongitudinalDTO getLongitudinalByEpisodeId(Integer episodeId) {
        EpisodeEntity episode = findEpisode(episodeId);
        return buildLongitudinal(episodeRepository.findHistoryByPostulantId(episode.getPostulant().getId()));
    }

    @Transactional(readOnly = true)
    public Page<PrioritizedEpisodeDTO> getPrioritized(Integer programId, String stateCode, String resultCode, Pageable pageable) {
        return episodeRepository.findPrioritized(programId, stateCode, resultCode, pageable)
                .map(this::toPrioritizedDTO);
    }

    @Transactional(readOnly = true)
    public DemandDashboardDTO getDashboard() {
        Page<PrioritizedEpisodeDTO> top = getPrioritized(null, null, null, PageRequest.of(0, 10));
        List<PrioritizedEpisodeDTO> active = getPrioritized(null, null, null, PageRequest.of(0, 1000)).getContent();

        Map<String, Long> semaphoreDistribution = active.stream()
                .collect(Collectors.groupingBy(PrioritizedEpisodeDTO::getSemaphoreColor, LinkedHashMap::new, Collectors.counting()));

        double averageDays = active.stream()
                .map(PrioritizedEpisodeDTO::getAccumulatedDays)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        long withoutFirstCitation = episodeRepository.countActiveWithoutFirstCitation();

        return DemandDashboardDTO.builder()
                .activeDemands(episodeRepository.countByActiveTrueAndDeletedAtIsNull())
                .waitingList(episodeRepository.countByActiveTrueAndResultCodeAndDeletedAtIsNull(RESULT_WAITING_LIST))
                .averageAccumulatedDays(Math.round(averageDays * 10.0) / 10.0)
                .redCases(semaphoreDistribution.getOrDefault("ROJO", 0L))
                .withoutFirstCitation(withoutFirstCitation)
                .openAlerts(alertRepository.countByStatusCodeIgnoreCaseAndDeletedAtIsNull("ACTIVA"))
                .semaphoreDistribution(semaphoreDistribution)
                .topCriticalCases(top.getContent())
                .build();
    }

    @Transactional
    public EpisodeEventDTO createEvent(Integer episodeId, CreateEventRequest request) {
        EpisodeEntity episode = findOpenEpisode(episodeId);
        EpisodeStageEntity stage = resolveStage(episode, request.getStageId());
        EventTypeEntity type = resolveEventType(request.getEventTypeId(), request.getEventTypeCode());
        AttendanceStatusEntity attendance = resolveAttendanceStatus(request.getAttendanceStatusId(), request.getAttendanceStatusCode(), null);
        UserEntity currentUser = currentUserOrNull();
        UserEntity professional = findNullable(userRepository, request.getProfessionalUserId());
        ProgramEntity program = request.getProgramId() != null
                ? programRepository.findById(request.getProgramId()).orElseThrow(() -> notFound("Programa no encontrado"))
                : stage.getProgram();
        EpisodeEventEntity relatedEvent = resolveRelatedEvent(episode, request.getRelatedEventId());

        EpisodeEventEntity event = EpisodeEventEntity.builder()
                .episode(episode)
                .stage(stage)
                .eventType(type)
                .relatedEvent(relatedEvent)
                .eventDate(request.getEventDate())
                .eventTime(request.getEventTime())
                .attendanceStatus(attendance)
                .professionName(request.getProfessionName())
                .professionalUser(professional)
                .registeredByUser(currentUser)
                .program(program)
                .comment(request.getComment())
                .citationComment(request.getCitationComment())
                .observation(request.getObservation())
                .nextAction(request.getNextAction())
                .nextActionDate(request.getNextActionDate())
                .resultCode(request.getResultCode())
                .stateCode(request.getStateCode())
                .build();
        event = eventRepository.save(event);
        updateRelatedCitationAttendanceIfNeeded(type, relatedEvent, attendance);

        if (hasText(request.getResultCode())) episode.setResultCode(request.getResultCode());
        if (hasText(request.getStateCode())) episode.setStateCode(request.getStateCode());
        episodeRepository.save(episode);

        audit(episode, stage, event, "REGISTRAR_EVENTO", null, type.getCode(), request.getComment(), currentUser, null, null);
        return toEventDTO(event);
    }

    @Transactional
    public EpisodeEventDTO createCitation(Integer episodeId, CreateCitationRequest request) {
        EpisodeEntity episode = findOpenEpisode(episodeId);
        EpisodeStageEntity stage = resolveStage(episode, request.getStageId());
        UserEntity professional = findNullable(userRepository, request.getProfessionalUserId());
        UserEntity currentUser = currentUserOrNull();
        ProgramEntity program = request.getProgramId() != null
                ? programRepository.findById(request.getProgramId()).orElseThrow(() -> notFound("Programa no encontrado"))
                : stage.getProgram();

        EpisodeEventEntity event = EpisodeEventEntity.builder()
                .episode(episode)
                .stage(stage)
                .eventType(eventType("CITACION"))
                .eventDate(request.getCitationDate())
                .eventTime(request.getCitationTime())
                .attendanceStatus(attendanceStatus("AGENDADO"))
                .professionName(request.getProfessionName())
                .professionalUser(professional)
                .registeredByUser(currentUser)
                .program(program)
                .citationComment(request.getCitationComment())
                .nextAction(request.getNextAction())
                .nextActionDate(request.getNextActionDate())
                .stateCode(STATE_IN_PROGRESS)
                .resultCode(episode.getResultCode())
                .build();
        event = eventRepository.save(event);
        audit(episode, stage, event, "REGISTRAR_CITACION", null, "Citación agendada", request.getCitationComment(), currentUser, null, null);
        return toEventDTO(event);
    }

    @Transactional
    public EpisodeEventDTO registerAttendance(Integer episodeId, RegisterAttendanceRequest request) {
        EpisodeEntity episode = findOpenEpisode(episodeId);
        EpisodeStageEntity stage = resolveStage(episode, request.getStageId());
        UserEntity professional = findNullable(userRepository, request.getProfessionalUserId());
        UserEntity currentUser = currentUserOrNull();
        AttendanceStatusEntity attendance = attendanceStatus(request.getAttendanceStatusCode());
        EpisodeEventEntity relatedEvent = resolveRelatedEvent(episode, request.getRelatedEventId());

        EpisodeEventEntity event = EpisodeEventEntity.builder()
                .episode(episode)
                .stage(stage)
                .eventType(eventType("ASISTENCIA"))
                .relatedEvent(relatedEvent)
                .eventDate(request.getEventDate())
                .eventTime(request.getEventTime())
                .attendanceStatus(attendance)
                .professionName(request.getProfessionName())
                .professionalUser(professional)
                .registeredByUser(currentUser)
                .program(stage.getProgram())
                .comment(request.getComment())
                .observation(request.getObservation())
                .nextAction(request.getNextAction())
                .nextActionDate(request.getNextActionDate())
                .resultCode(request.getResultCode())
                .stateCode(STATE_IN_PROGRESS)
                .build();
        event = eventRepository.save(event);
        updateRelatedCitationAttendanceIfNeeded(event.getEventType(), relatedEvent, attendance);

        if (hasText(request.getResultCode())) {
            episode.setResultCode(request.getResultCode());
            stage.setResultCode(request.getResultCode());
        }

        if ("NO_SE_PRESENTO".equalsIgnoreCase(attendance.getCode())) {
            Integer professionalUserId = professional != null ? professional.getId() : null;
            long noShows = eventRepository.countNoShowByStageAndProfessional(stage.getId(), professionalUserId);
            if (noShows >= 2) {
                ClosureReasonEntity reason = closureReason("CIERRE_POR_INASISTENCIAS");
                closeEpisodeInternal(episode, stage, reason, "Cierre automático: dos inasistencias con el mismo profesional.", currentUser, "CIERRE_POR_INASISTENCIAS");
                audit(episode, stage, event, "CIERRE_POR_INASISTENCIAS", null, "Episodio cerrado por dos inasistencias", request.getComment(), currentUser, null, null);
            }
        }

        episodeRepository.save(episode);
        stageRepository.save(stage);
        audit(episode, stage, event, "REGISTRAR_ASISTENCIA", null, attendance.getCode(), request.getComment(), currentUser, null, null);
        return toEventDTO(event);
    }

    @Transactional
    public EpisodeReferenceDTO referenceEpisode(Integer episodeId, ReferenceEpisodeRequest request) {
        requireConfirmation(request.getConfirmImpact(), "La referencia cerrará la etapa de origen, creará una etapa receptora y mantendrá los días acumulados.");
        EpisodeEntity episode = findOpenEpisode(episodeId);
        EpisodeStageEntity originStage = resolveStage(episode, request.getOriginStageId());
        ProgramEntity destinationProgram = programRepository.findById(request.getDestinationProgramId())
                .orElseThrow(() -> notFound("Programa receptor no encontrado"));
        UserEntity currentUser = currentUserOrNull();

        originStage.setCurrent(false);
        originStage.setClosedAt(LocalDateTime.now());
        originStage.setResultCode(RESULT_REFERENCE);
        originStage.setStateCode(STATE_CLOSED);
        stageRepository.save(originStage);

        EpisodeStageEntity destinationStage = EpisodeStageEntity.builder()
                .episode(episode)
                .program(destinationProgram)
                .stageOrder(stageRepository.findMaxStageOrder(episode.getId()) + 1)
                .originStage(originStage)
                .receivedAt(LocalDateTime.now())
                .stateCode(STATE_IN_PROGRESS)
                .resultCode(RESULT_PENDING)
                .current(true)
                .responsibleUser(currentUser)
                .build();
        destinationStage = stageRepository.save(destinationStage);

        episode.setCurrentProgram(destinationProgram);
        episode.setCurrentStage(destinationStage);
        episode.setStateCode(STATE_IN_PROGRESS);
        episode.setResultCode(RESULT_REFERENCE);
        episodeRepository.save(episode);

        EpisodeReferenceEntity reference = EpisodeReferenceEntity.builder()
                .episode(episode)
                .originStage(originStage)
                .destinationStage(destinationStage)
                .originProgram(originStage.getProgram())
                .destinationProgram(destinationProgram)
                .reason(request.getReason())
                .observation(request.getObservation())
                .createdByUser(currentUser)
                .build();
        reference = referenceRepository.save(reference);

        EpisodeEventEntity event = createInternalEvent(episode, originStage, "REFERENCIA", null, null, originStage.getProgram(), currentUser, currentUser,
                "Referencia a " + destinationProgram.getName(), null, request.getObservation(), null, null, RESULT_REFERENCE, STATE_CLOSED);
        audit(episode, originStage, event, "REFERENCIA", originStage.getProgram().getName(), destinationProgram.getName(), request.getReason(), currentUser, null, null);
        return toReferenceDTO(reference);
    }

    @Transactional
    public EpisodeDTO registerTreatmentEntry(Integer episodeId, TreatmentEntryRequest request) {
        requireConfirmation(request.getConfirmImpact(), "El ingreso a tratamiento detendrá el KPI de espera.");
        EpisodeEntity episode = findOpenEpisode(episodeId);
        EpisodeStageEntity stage = resolveStage(episode, null);
        UserEntity currentUser = currentUserOrNull();

        LocalDateTime entryAt = request.getEntryToTreatmentAt() != null ? request.getEntryToTreatmentAt() : LocalDateTime.now();
        episode.setEntryToTreatmentAt(entryAt);
        episode.setWaitingStopped(true);
        episode.setResultCode(RESULT_TREATMENT_ENTRY);
        stage.setResultCode(RESULT_TREATMENT_ENTRY);
        episodeRepository.save(episode);
        stageRepository.save(stage);

        EpisodeEventEntity event = createInternalEvent(episode, stage, "INGRESO_TRATAMIENTO", null, null, stage.getProgram(), currentUser, currentUser,
                "Ingreso efectivo a tratamiento", null, request.getComment(), null, null, RESULT_TREATMENT_ENTRY, STATE_IN_PROGRESS);
        audit(episode, stage, event, "INGRESO_TRATAMIENTO", null, entryAt.toString(), request.getComment(), currentUser, null, null);
        return toEpisodeDTO(episode);
    }

    @Transactional
    public EpisodeDTO egressEpisode(Integer episodeId, EgressEpisodeRequest request) {
        requireConfirmation(request.getConfirmImpact(), "El egreso cerrará definitivamente el episodio en operación normal.");
        EpisodeEntity episode = findOpenEpisode(episodeId);
        EpisodeStageEntity stage = resolveStage(episode, null);
        UserEntity currentUser = currentUserOrNull();
        LocalDateTime egressAt = request.getEgressAt() != null ? request.getEgressAt() : LocalDateTime.now();
        ClosureReasonEntity reason = closureReason("EGRESO");

        episode.setEgressAt(egressAt);
        closeEpisodeInternal(episode, stage, reason, request.getComment(), currentUser, RESULT_EGRESS);
        EpisodeEventEntity event = createInternalEvent(episode, stage, "EGRESO", null, null, stage.getProgram(), currentUser, currentUser,
                "Egreso del proceso de tratamiento", null, request.getComment(), null, null, RESULT_EGRESS, STATE_CLOSED);
        audit(episode, stage, event, "EGRESO", null, egressAt.toString(), request.getComment(), currentUser, null, null);
        return toEpisodeDTO(episode);
    }

    @Transactional
    public EpisodeDTO closeEpisode(Integer episodeId, CloseEpisodeRequest request) {
        requireConfirmation(request.getConfirmImpact(), "El cierre pasará el episodio a histórico y no podrá editarse libremente.");
        EpisodeEntity episode = findOpenEpisode(episodeId);
        EpisodeStageEntity stage = resolveStage(episode, null);
        UserEntity currentUser = currentUserOrNull();
        ClosureReasonEntity reason = resolveClosureReason(request.getClosureReasonId(), request.getClosureReasonCode());

        if (reason.getCode() != null && reason.getCode().toUpperCase().contains("OTRO") && !hasText(request.getClosureComment())) {
            throw badRequest("Cuando la causal es OTRO, la observación/comentario de cierre es obligatoria.");
        }

        closeEpisodeInternal(episode, stage, reason, request.getClosureComment(), currentUser, RESULT_CLOSURE);
        EpisodeEventEntity event = createInternalEvent(episode, stage, "CIERRE", null, null, stage.getProgram(), currentUser, currentUser,
                "Cierre de episodio: " + reason.getName(), null, request.getClosureComment(), null, null, RESULT_CLOSURE, STATE_CLOSED);
        audit(episode, stage, event, "CIERRE_EPISODIO", null, reason.getCode(), request.getClosureComment(), currentUser, null, null);
        return toEpisodeDTO(episode);
    }

    @Transactional
    public EpisodeDTO reverseEpisode(Integer episodeId, ReverseEpisodeRequest request) {
        EpisodeEntity episode = findEpisode(episodeId);
        UserEntity currentUser = currentUserOrNull();
        if (episode.getClosedAt() == null && Boolean.TRUE.equals(episode.getActive())) {
            throw badRequest("El episodio ya se encuentra activo; no requiere reversión.");
        }

        episode.setActive(true);
        episode.setClosedAt(null);
        episode.setClosureReason(null);
        episode.setClosureComment(null);
        episode.setClosedByUser(null);
        episode.setReversedByUser(currentUser);
        episode.setStateCode(STATE_IN_PROGRESS);
        episode.setResultCode(RESULT_PENDING);
        episodeRepository.save(episode);

        EpisodeStageEntity stage = stageRepository.findFirstByEpisodeIdAndCurrentTrueOrderByStageOrderDesc(episode.getId())
                .orElseGet(() -> stageRepository.findByEpisodeIdOrderByStageOrderAsc(episode.getId()).stream()
                        .max(Comparator.comparing(EpisodeStageEntity::getStageOrder))
                        .orElseThrow(() -> notFound("No existe etapa para revertir")));
        stage.setCurrent(true);
        stage.setClosedAt(null);
        stage.setStateCode(STATE_IN_PROGRESS);
        stage.setResultCode(RESULT_PENDING);
        stageRepository.save(stage);
        episode.setCurrentStage(stage);
        episode.setCurrentProgram(stage.getProgram());
        episodeRepository.save(episode);

        EpisodeEventEntity event = createInternalEvent(episode, stage, "REVERSION", null, null, stage.getProgram(), currentUser, currentUser,
                "Reversión por perfil superior", null, request.getReason(), null, null, RESULT_PENDING, STATE_IN_PROGRESS);
        audit(episode, stage, event, "REVERSION", STATE_CLOSED, STATE_IN_PROGRESS, request.getReason(), currentUser, currentUser, currentUser);
        return toEpisodeDTO(episode);
    }

    @Transactional
    public EpisodeAlertDTO createAlert(Integer episodeId, CreateAlertRequest request) {
        EpisodeEntity episode = findEpisode(episodeId);
        EpisodeStageEntity stage = request.getStageId() != null ? stageRepository.findById(request.getStageId()).orElseThrow(() -> notFound("Etapa no encontrada")) : episode.getCurrentStage();
        UserEntity currentUser = currentUserOrNull();
        UserEntity responsible = findNullable(userRepository, request.getResponsibleUserId());

        EpisodeAlertEntity alert = EpisodeAlertEntity.builder()
                .episode(episode)
                .stage(stage)
                .alertTypeCode(request.getAlertTypeCode())
                .priorityLevelCode(request.getPriorityLevelCode())
                .description(request.getDescription())
                .actionTaken(request.getActionTaken())
                .nextAction(request.getNextAction())
                .nextActionDate(request.getNextActionDate())
                .responsibleUser(responsible)
                .statusCode(hasText(request.getStatusCode()) ? request.getStatusCode() : "ACTIVA")
                .createdByUser(currentUser)
                .build();
        alert = alertRepository.save(alert);
        audit(episode, stage, null, "CREAR_ALERTA", null, alert.getPriorityLevelCode(), alert.getDescription(), currentUser, null, null);
        return toAlertDTO(alert);
    }

    @Transactional(readOnly = true)
    public List<EpisodeDocumentDTO> listDocuments(Integer episodeId) {
        findEpisode(episodeId);
        return documentRepository.findByEpisodeIdOrderByUploadedAtDesc(episodeId)
                .stream()
                .map(this::toDocumentDTO)
                .toList();
    }

    @Transactional
    public EpisodeDocumentDTO createDocument(Integer episodeId, CreateDocumentRequest request) {
        EpisodeEntity episode = findEpisode(episodeId);
        EpisodeStageEntity stage = request.getStageId() != null ? stageRepository.findById(request.getStageId()).orElseThrow(() -> notFound("Etapa no encontrada")) : null;
        EpisodeEventEntity event = request.getEventId() != null ? eventRepository.findById(request.getEventId()).orElseThrow(() -> notFound("Evento no encontrado")) : null;
        EpisodeReferenceEntity reference = request.getReferenceId() != null ? referenceRepository.findById(request.getReferenceId()).orElseThrow(() -> notFound("Referencia no encontrada")) : null;
        UserEntity currentUser = currentUserOrNull();

        if (hasText(request.getDocumentTypeCode())) validateDocumentType(request.getDocumentTypeCode());

        EpisodeDocumentEntity document = EpisodeDocumentEntity.builder()
                .episode(episode)
                .stage(stage)
                .event(event)
                .reference(reference)
                .documentTypeCode(normalizeCode(request.getDocumentTypeCode()))
                .originalFilename(request.getOriginalFilename())
                .storedFilename(request.getStoredFilename())
                .storagePath(request.getStoragePath())
                .mimeType(request.getMimeType())
                .fileSize(request.getFileSize())
                .uploadedByUser(currentUser)
                .build();
        document = documentRepository.save(document);
        audit(episode, stage, event, "ADJUNTAR_DOCUMENTO_METADATA", null, document.getOriginalFilename(), document.getDocumentTypeCode(), currentUser, null, null);
        return toDocumentDTO(document);
    }

    @Transactional
    public EpisodeDocumentDTO uploadDocument(Integer episodeId, MultipartFile file, String documentTypeCode, Integer stageId, Integer eventId, Integer referenceId) {
        if (file == null || file.isEmpty()) throw badRequest("Debe adjuntar un archivo en el campo file.");
        EpisodeEntity episode = findEpisode(episodeId);
        EpisodeStageEntity stage = stageId != null ? stageRepository.findById(stageId).orElseThrow(() -> notFound("Etapa no encontrada")) : null;
        EpisodeEventEntity event = eventId != null ? eventRepository.findById(eventId).orElseThrow(() -> notFound("Evento no encontrado")) : null;
        EpisodeReferenceEntity reference = referenceId != null ? referenceRepository.findById(referenceId).orElseThrow(() -> notFound("Referencia no encontrada")) : null;
        UserEntity currentUser = currentUserOrNull();
        validateDocumentType(documentTypeCode);

        String originalFilename = cleanFilename(file.getOriginalFilename());
        String extension = extensionOf(originalFilename);
        String storedFilename = "episode-" + episodeId + "-" + UUID.randomUUID() + extension;
        Path dir = Paths.get(documentStorageDir, "episodes", String.valueOf(episodeId)).toAbsolutePath().normalize();
        Path target = dir.resolve(storedFilename).normalize();
        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No fue posible guardar el archivo: " + ex.getMessage());
        }

        EpisodeDocumentEntity document = EpisodeDocumentEntity.builder()
                .episode(episode)
                .stage(stage)
                .event(event)
                .reference(reference)
                .documentTypeCode(normalizeCode(documentTypeCode))
                .originalFilename(originalFilename)
                .storedFilename(storedFilename)
                .storagePath(target.toString())
                .mimeType(hasText(file.getContentType()) ? file.getContentType() : "application/octet-stream")
                .fileSize(file.getSize())
                .uploadedByUser(currentUser)
                .uploadedAt(LocalDateTime.now())
                .build();
        document = documentRepository.save(document);
        audit(episode, stage, event, "SUBIR_DOCUMENTO", null, document.getOriginalFilename(), document.getDocumentTypeCode(), currentUser, null, null);
        return toDocumentDTO(document);
    }

    @Transactional(readOnly = true)
    public DocumentDownloadDTO downloadDocument(Integer documentId) {
        EpisodeDocumentEntity document = documentRepository.findById(documentId).orElseThrow(() -> notFound("Documento no encontrado"));
        if (!hasText(document.getStoragePath())) throw notFound("El documento no tiene archivo físico asociado.");
        Path path = Paths.get(document.getStoragePath()).toAbsolutePath().normalize();
        if (!Files.exists(path) || !Files.isRegularFile(path)) throw notFound("Archivo físico no encontrado.");
        Resource resource = new FileSystemResource(path);
        return DocumentDownloadDTO.builder()
                .resource(resource)
                .filename(hasText(document.getOriginalFilename()) ? document.getOriginalFilename() : document.getStoredFilename())
                .mimeType(hasText(document.getMimeType()) ? document.getMimeType() : "application/octet-stream")
                .fileSize(document.getFileSize())
                .build();
    }

    @Transactional
    public EpisodeDocumentDTO updateDocument(Integer documentId, UpdateDocumentRequest request) {
        EpisodeDocumentEntity document = documentRepository.findById(documentId).orElseThrow(() -> notFound("Documento no encontrado"));
        String previous = documentSnapshot(document);
        EpisodeStageEntity stage = request.getStageId() != null ? stageRepository.findById(request.getStageId()).orElseThrow(() -> notFound("Etapa no encontrada")) : document.getStage();
        EpisodeEventEntity event = request.getEventId() != null ? eventRepository.findById(request.getEventId()).orElseThrow(() -> notFound("Evento no encontrado")) : document.getEvent();
        EpisodeReferenceEntity reference = request.getReferenceId() != null ? referenceRepository.findById(request.getReferenceId()).orElseThrow(() -> notFound("Referencia no encontrada")) : document.getReference();

        if (hasText(request.getDocumentTypeCode())) {
            validateDocumentType(request.getDocumentTypeCode());
            document.setDocumentTypeCode(normalizeCode(request.getDocumentTypeCode()));
        }
        if (hasText(request.getOriginalFilename())) document.setOriginalFilename(cleanFilename(request.getOriginalFilename()));
        document.setStage(stage);
        document.setEvent(event);
        document.setReference(reference);
        document = documentRepository.save(document);
        audit(document.getEpisode(), stage, event, "MODIFICAR_DOCUMENTO", previous, documentSnapshot(document), "Actualización de metadata", currentUserOrNull(), null, null);
        return toDocumentDTO(document);
    }

    @Transactional
    public void deleteDocument(Integer documentId) {
        EpisodeDocumentEntity document = documentRepository.findById(documentId).orElseThrow(() -> notFound("Documento no encontrado"));
        audit(document.getEpisode(), document.getStage(), document.getEvent(), "ELIMINAR_DOCUMENTO", documentSnapshot(document), "deleted_at", "Eliminación lógica de documento", currentUserOrNull(), null, null);
        documentRepository.delete(document);
    }

    @Transactional
    public EpisodeDocumentDTO replaceDocument(Integer documentId, MultipartFile file, String documentTypeCode, Integer stageId, Integer eventId, Integer referenceId) {
        EpisodeDocumentEntity previous = documentRepository.findById(documentId).orElseThrow(() -> notFound("Documento no encontrado"));
        Integer episodeId = previous.getEpisode() != null ? previous.getEpisode().getId() : null;
        if (episodeId == null) throw badRequest("El documento no tiene episodio asociado.");
        audit(previous.getEpisode(), previous.getStage(), previous.getEvent(), "REEMPLAZAR_DOCUMENTO_ANTERIOR", documentSnapshot(previous), "deleted_at", "Reemplazo de archivo", currentUserOrNull(), null, null);
        documentRepository.delete(previous);
        EpisodeDocumentDTO created = uploadDocument(episodeId, file, hasText(documentTypeCode) ? documentTypeCode : previous.getDocumentTypeCode(), stageId, eventId, referenceId);
        audit(previous.getEpisode(), previous.getStage(), previous.getEvent(), "REEMPLAZAR_DOCUMENTO_NUEVO", null, String.valueOf(created.getId()), "Nuevo documento creado por reemplazo", currentUserOrNull(), null, null);
        return created;
    }

    @Transactional
    public EmailNotificationResponse sendEmailNotification(EmailNotificationRequest request) {
        EpisodeEntity episode = request.getEpisodeId() != null ? findEpisode(request.getEpisodeId()) : null;
        EpisodeDocumentEntity document = request.getDocumentId() != null ? documentRepository.findById(request.getDocumentId()).orElseThrow(() -> notFound("Documento no encontrado")) : null;
        if (episode == null && document != null) episode = document.getEpisode();
        audit(episode, episode != null ? episode.getCurrentStage() : null, null, "SOLICITUD_ENVIO_CORREO", null, request.getTo(), request.getSubject(), currentUserOrNull(), null, null);
        return EmailNotificationResponse.builder()
                .sent(false)
                .queued(false)
                .result("EMAIL_SERVICE_NOT_CONFIGURED")
                .message("Endpoint habilitado. El envío SMTP real queda pendiente de configuración institucional.")
                .episodeId(episode != null ? episode.getId() : request.getEpisodeId())
                .documentId(document != null ? document.getId() : request.getDocumentId())
                .to(request.getTo())
                .subject(request.getSubject())
                .documentIds(request.getDocumentIds())
                .sentAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public EpisodeSubstanceDTO addSubstance(Integer episodeId, CreateSubstanceRequest request) {
        EpisodeEntity episode = findEpisode(episodeId);
        SubstanceEntity substance = substanceRepository.findById(request.getSubstanceId())
                .orElseThrow(() -> notFound("Sustancia no encontrada"));
        EpisodeSubstanceEntity entity = EpisodeSubstanceEntity.builder()
                .episode(episode)
                .substance(substance)
                .level(request.getLevel())
                .primarySubstance(request.getPrimarySubstance() != null ? request.getPrimarySubstance() : isPrimaryLevel(request.getLevel(), request.getUseOrder()))
                .useOrder(request.getUseOrder() != null ? request.getUseOrder() : parseLevelAsOrder(request.getLevel()))
                .observation(request.getObservation())
                .build();
        entity = episodeSubstanceRepository.save(entity);
        audit(episode, episode.getCurrentStage(), null, "REGISTRAR_SUSTANCIA", null, substance.getName(), request.getObservation(), currentUserOrNull(), null, null);
        return toSubstanceDTO(entity);
    }

    private EpisodeLongitudinalDTO buildLongitudinal(List<EpisodeEntity> episodes) {
        EpisodeEntity first = episodes.get(0);
        List<Integer> episodeIds = episodes.stream().map(EpisodeEntity::getId).toList();
        List<EpisodeStageDTO> stages = new ArrayList<>();
        List<EpisodeEventDTO> events = new ArrayList<>();
        List<EpisodeReferenceDTO> references = new ArrayList<>();
        List<EpisodeAlertDTO> alerts = new ArrayList<>();
        List<EpisodeDocumentDTO> documents = new ArrayList<>();
        List<EpisodeAuditLogDTO> auditLogs = new ArrayList<>();

        for (Integer id : episodeIds) {
            stages.addAll(stageRepository.findByEpisodeIdOrderByStageOrderAsc(id).stream().map(this::toStageDTO).toList());
            events.addAll(eventRepository.findByEpisodeIdOrderByEventDateAscEventTimeAscIdAsc(id).stream().map(this::toEventDTO).toList());
            references.addAll(referenceRepository.findByEpisodeIdOrderByReferenceDateAsc(id).stream().map(this::toReferenceDTO).toList());
            alerts.addAll(alertRepository.findByEpisodeIdOrderByCreatedAtDesc(id).stream().map(this::toAlertDTO).toList());
            documents.addAll(documentRepository.findByEpisodeIdOrderByUploadedAtDesc(id).stream().map(this::toDocumentDTO).toList());
            auditLogs.addAll(auditLogRepository.findByEpisodeIdOrderByPerformedAtDesc(id).stream().map(this::toAuditDTO).toList());
        }

        return EpisodeLongitudinalDTO.builder()
                .postulant(toPostulantDTO(first.getPostulant()))
                .activeEpisode(episodes.stream().filter(EpisodeEntity::getActive).findFirst().map(this::toEpisodeDTO).orElse(null))
                .episodes(episodes.stream().map(this::toEpisodeDTO).toList())
                .stages(stages)
                .events(events)
                .references(references)
                .alerts(alerts)
                .documents(documents)
                .auditLogs(auditLogs)
                .build();
    }

    private EpisodeEntity findEpisode(Integer episodeId) {
        return episodeRepository.findById(episodeId).orElseThrow(() -> notFound("Episodio no encontrado"));
    }

    private EpisodeEntity findOpenEpisode(Integer episodeId) {
        EpisodeEntity episode = findEpisode(episodeId);
        if (!Boolean.TRUE.equals(episode.getActive()) || episode.getClosedAt() != null) {
            throw badRequest("El episodio se encuentra cerrado. Debe usar reversión por perfil superior si corresponde.");
        }
        return episode;
    }

    private EpisodeStageEntity resolveStage(EpisodeEntity episode, Integer stageId) {
        if (stageId != null) {
            EpisodeStageEntity stage = stageRepository.findById(stageId).orElseThrow(() -> notFound("Etapa no encontrada"));
            if (!Objects.equals(stage.getEpisode().getId(), episode.getId())) throw badRequest("La etapa no pertenece al episodio indicado.");
            return stage;
        }
        if (episode.getCurrentStage() != null) return episode.getCurrentStage();
        return stageRepository.findFirstByEpisodeIdAndCurrentTrueOrderByStageOrderDesc(episode.getId())
                .orElseThrow(() -> notFound("El episodio no tiene etapa activa"));
    }

    private void closeEpisodeInternal(EpisodeEntity episode, EpisodeStageEntity stage, ClosureReasonEntity reason, String comment, UserEntity currentUser, String resultCode) {
        LocalDateTime now = LocalDateTime.now();
        episode.setActive(false);
        episode.setClosedAt(now);
        episode.setStateCode(STATE_CLOSED);
        episode.setResultCode(resultCode);
        episode.setClosureReason(reason);
        episode.setClosureComment(comment);
        episode.setClosedByUser(currentUser);
        if (stage != null) {
            stage.setCurrent(false);
            stage.setClosedAt(now);
            stage.setStateCode(STATE_CLOSED);
            stage.setResultCode(resultCode);
            stage.setClosureReason(reason);
            stage.setClosureComment(comment);
            stageRepository.save(stage);
        }
        episodeRepository.save(episode);
    }

    private EpisodeEventEntity createInternalEvent(EpisodeEntity episode, EpisodeStageEntity stage, String eventTypeCode,
                                                   AttendanceStatusEntity attendance, String professionName, ProgramEntity program,
                                                   UserEntity professional, UserEntity registeredBy, String comment,
                                                   String citationComment, String observation, String nextAction, LocalDate nextActionDate,
                                                   String resultCode, String stateCode) {
        EpisodeEventEntity event = EpisodeEventEntity.builder()
                .episode(episode)
                .stage(stage)
                .eventType(eventType(eventTypeCode))
                .attendanceStatus(attendance)
                .professionName(professionName)
                .professionalUser(professional)
                .registeredByUser(registeredBy)
                .program(program)
                .comment(comment)
                .citationComment(citationComment)
                .observation(observation)
                .nextAction(nextAction)
                .nextActionDate(nextActionDate)
                .resultCode(resultCode)
                .stateCode(stateCode)
                .build();
        return eventRepository.save(event);
    }

    private void audit(EpisodeEntity episode, EpisodeStageEntity stage, EpisodeEventEntity event, String actionType,
                       String previousValue, String newValue, String reason, UserEntity performedBy,
                       UserEntity authorizedBy, UserEntity reversedBy) {
        auditLogRepository.save(EpisodeAuditLogEntity.builder()
                .episode(episode)
                .stage(stage)
                .event(event)
                .actionType(actionType)
                .previousValue(previousValue)
                .newValue(newValue)
                .reason(reason)
                .performedByUser(performedBy)
                .authorizedByUser(authorizedBy)
                .reversedByUser(reversedBy)
                .build());
    }

    private EpisodeTypeEntity resolveEpisodeType(Integer id, String code, String defaultCode) {
        if (id != null) return episodeTypeRepository.findById(id).orElseThrow(() -> notFound("Tipo de episodio no encontrado"));
        return episodeTypeRepository.findByCodeIgnoreCase(hasText(code) ? code : defaultCode)
                .orElseThrow(() -> notFound("Tipo de episodio no encontrado: " + (hasText(code) ? code : defaultCode)));
    }

    private EventTypeEntity resolveEventType(Integer id, String code) {
        if (id != null) return eventTypeRepository.findById(id).orElseThrow(() -> notFound("Tipo de evento no encontrado"));
        if (!hasText(code)) throw badRequest("Debe indicar eventTypeCode o eventTypeId");
        return eventType(code);
    }

    private EventTypeEntity eventType(String code) {
        return eventTypeRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> notFound("Tipo de evento no encontrado: " + code));
    }

    private EpisodeEventEntity resolveRelatedEvent(EpisodeEntity episode, Integer relatedEventId) {
        if (relatedEventId == null) return null;
        EpisodeEventEntity related = eventRepository.findById(relatedEventId)
                .orElseThrow(() -> notFound("Evento relacionado no encontrado: " + relatedEventId));
        if (related.getEpisode() == null || !Objects.equals(related.getEpisode().getId(), episode.getId())) {
            throw badRequest("El evento relacionado no pertenece al episodio indicado");
        }
        return related;
    }

    private void updateRelatedCitationAttendanceIfNeeded(EventTypeEntity eventType, EpisodeEventEntity relatedEvent, AttendanceStatusEntity attendance) {
        if (eventType == null || relatedEvent == null || attendance == null) return;
        if (!"ASISTENCIA".equalsIgnoreCase(eventType.getCode())) return;
        if (relatedEvent.getEventType() == null || !"CITACION".equalsIgnoreCase(relatedEvent.getEventType().getCode())) return;
        relatedEvent.setAttendanceStatus(attendance);
        relatedEvent.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(relatedEvent);
    }

    private AttendanceStatusEntity resolveAttendanceStatus(Integer id, String code, String defaultCode) {
        if (id != null) return attendanceStatusRepository.findById(id).orElseThrow(() -> notFound("Estado de asistencia no encontrado"));
        if (!hasText(code) && !hasText(defaultCode)) return null;
        return attendanceStatus(hasText(code) ? code : defaultCode);
    }

    private AttendanceStatusEntity attendanceStatus(String code) {
        return attendanceStatusRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> notFound("Estado de asistencia no encontrado: " + code));
    }

    private ClosureReasonEntity resolveClosureReason(Integer id, String code) {
        if (id != null) return closureReasonRepository.findById(id).orElseThrow(() -> notFound("Motivo de cierre no encontrado"));
        if (!hasText(code)) throw badRequest("Debe indicar closureReasonCode o closureReasonId");
        return closureReason(code);
    }

    private ClosureReasonEntity closureReason(String code) {
        return closureReasonRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> notFound("Motivo de cierre no encontrado: " + code));
    }

    private <T> T findNullable(org.springframework.data.jpa.repository.JpaRepository<T, Integer> repository, Integer id) {
        if (id == null) return null;
        return repository.findById(id).orElseThrow(() -> notFound("Registro relacionado no encontrado: " + id));
    }

    private UserEntity currentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) return null;
        return userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
    }

    private void requireConfirmation(Boolean confirmImpact, String message) {
        if (!Boolean.TRUE.equals(confirmImpact)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, message + " Debe confirmar con confirmImpact=true.");
        }
    }

    private String normalizeRut(String rut) {
        return rut == null ? "" : rut.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private List<OptionDTO> alertTypes() {
        return List.of(
                OptionDTO.builder().code("ALERTA_ESPERA").name("Alerta por espera").build(),
                OptionDTO.builder().code("ALERTA_INASISTENCIA").name("Alerta por inasistencia").build(),
                OptionDTO.builder().code("ALERTA_DOCUMENTO").name("Alerta por documento").build(),
                OptionDTO.builder().code("ALERTA_REFERENCIA").name("Alerta por referencia").build()
        );
    }

    private List<OptionDTO> priorityLevels() {
        return List.of(
                OptionDTO.builder().code("BAJA").name("Baja").build(),
                OptionDTO.builder().code("MEDIA").name("Media").build(),
                OptionDTO.builder().code("ALTA").name("Alta").build(),
                OptionDTO.builder().code("CRITICA").name("Crítica").build()
        );
    }

    private List<OptionDTO> alertStatuses() {
        return List.of(
                OptionDTO.builder().code("ACTIVA").name("Activa").build(),
                OptionDTO.builder().code("GESTIONADA").name("Gestionada").build(),
                OptionDTO.builder().code("CERRADA").name("Cerrada").build(),
                OptionDTO.builder().code("DESCARTADA").name("Descartada").build()
        );
    }

    private void validateDocumentType(String code) {
        if (!hasText(code)) throw badRequest("Debe indicar documentTypeCode.");
        documentTypeRepository.findByCodeIgnoreCase(normalizeCode(code))
                .orElseThrow(() -> badRequest("Tipo de documento no válido: " + code));
    }

    private String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase().replace(' ', '_');
    }

    private String cleanFilename(String filename) {
        if (!hasText(filename)) return "documento";
        return Paths.get(filename).getFileName().toString().replaceAll("[/\\\\]+", "_");
    }

    private String extensionOf(String filename) {
        if (!hasText(filename)) return "";
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) return "";
        String ext = filename.substring(idx);
        return ext.length() > 20 ? "" : ext;
    }

    private String documentSnapshot(EpisodeDocumentEntity document) {
        if (document == null) return null;
        return "id=" + document.getId()
                + ";type=" + document.getDocumentTypeCode()
                + ";original=" + document.getOriginalFilename()
                + ";stored=" + document.getStoredFilename()
                + ";path=" + document.getStoragePath();
    }

    private Boolean isPrimaryLevel(String level, Integer useOrder) {
        if (useOrder != null) return useOrder == 1;
        Integer parsed = parseLevelAsOrder(level);
        return parsed != null && parsed == 1;
    }

    private Integer parseLevelAsOrder(String level) {
        if (!hasText(level)) return null;
        try { return Integer.parseInt(level.trim()); } catch (Exception ignored) { return null; }
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private OptionDTO toOption(EpisodeTypeEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(EventTypeEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(AttendanceStatusEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(ClosureReasonEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(ProgramPopulationEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(ProgramModalityEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(ProgramPlanEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(DocumentTypeEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(RegionEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(CityEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }

    private UserSummaryDTO toUserDTO(UserEntity user) {
        if (user == null) return null;
        String name = String.join(" ", Arrays.asList(
                        Optional.ofNullable(user.getFirstName()).orElse(""),
                        Optional.ofNullable(user.getSecondName()).orElse(""),
                        Optional.ofNullable(user.getFirstLastName()).orElse(""),
                        Optional.ofNullable(user.getSecondLastName()).orElse("")))
                .replaceAll("\\s+", " ").trim();
        return UserSummaryDTO.builder()
                .id(user.getId())
                .name(hasText(name) ? name : user.getUsername())
                .email(user.getEmail())
                .build();
    }

    private PostulantSummaryDTO toPostulantDTO(PostulantEntity p) {
        if (p == null) return null;
        return PostulantSummaryDTO.builder()
                .id(p.getId())
                .rut(p.getRut())
                .firstName(p.getFirstName())
                .firstLastName(p.getFirstLastName())
                .secondLastName(p.getSecondLastName())
                .birthdate(p.getBirthdate())
                .email(p.getEmail())
                .phone(p.getPhone())
                .address(p.getAddress())
                .build();
    }

    private ProgramSummaryDTO toProgramDTO(ProgramEntity p) {
        if (p == null) return null;
        return ProgramSummaryDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .populationType(toOption(p.getPopulationType()))
                .modality(toOption(p.getModality()))
                .plan(toOption(p.getPlan()))
                .region(toOption(p.getRegion()))
                .city(toOption(p.getCity()))
                .address(p.getAddress())
                .phone(p.getPhone())
                .email(p.getEmail())
                .active(p.getActive())
                .build();
    }

    private EpisodeDTO toEpisodeDTO(EpisodeEntity e) {
        if (e == null) return null;
        int days = accumulatedDays(e);
        return EpisodeDTO.builder()
                .id(e.getId())
                .episodeCode(e.getEpisodeCode())
                .postulant(toPostulantDTO(e.getPostulant()))
                .episodeType(toOption(e.getEpisodeType()))
                .originalRequestDate(e.getOriginalRequestDate())
                .initialProgram(toProgramDTO(e.getInitialProgram()))
                .currentProgram(toProgramDTO(e.getCurrentProgram()))
                .currentStageId(e.getCurrentStage() != null ? e.getCurrentStage().getId() : null)
                .stateCode(e.getStateCode())
                .resultCode(e.getResultCode())
                .entryToTreatmentAt(e.getEntryToTreatmentAt())
                .egressAt(e.getEgressAt())
                .closedAt(e.getClosedAt())
                .closureReason(toOption(e.getClosureReason()))
                .closureComment(e.getClosureComment())
                .active(e.getActive())
                .waitingStopped(e.getWaitingStopped())
                .accumulatedDays(days)
                .semaphoreColor(resolveSemaphore(days))
                .createdByUser(toUserDTO(e.getCreatedByUser()))
                .closedByUser(toUserDTO(e.getClosedByUser()))
                .reversedByUser(toUserDTO(e.getReversedByUser()))
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private EpisodeStageDTO toStageDTO(EpisodeStageEntity s) {
        if (s == null) return null;
        long days = s.getReceivedAt() == null ? 0 : ChronoUnit.DAYS.between(s.getReceivedAt().toLocalDate(),
                (s.getClosedAt() != null ? s.getClosedAt() : LocalDateTime.now()).toLocalDate());
        return EpisodeStageDTO.builder()
                .id(s.getId())
                .stageOrder(s.getStageOrder())
                .program(toProgramDTO(s.getProgram()))
                .originStageId(s.getOriginStage() != null ? s.getOriginStage().getId() : null)
                .receivedAt(s.getReceivedAt())
                .closedAt(s.getClosedAt())
                .stateCode(s.getStateCode())
                .resultCode(s.getResultCode())
                .closureReason(toOption(s.getClosureReason()))
                .closureComment(s.getClosureComment())
                .current(s.getCurrent())
                .responsibleUser(toUserDTO(s.getResponsibleUser()))
                .daysInStage((int) days)
                .build();
    }

    private EpisodeEventDTO toEventDTO(EpisodeEventEntity ev) {
        if (ev == null) return null;
        return EpisodeEventDTO.builder()
                .id(ev.getId())
                .episodeId(ev.getEpisode() != null ? ev.getEpisode().getId() : null)
                .stageId(ev.getStage() != null ? ev.getStage().getId() : null)
                .relatedEventId(ev.getRelatedEvent() != null ? ev.getRelatedEvent().getId() : null)
                .eventType(toOption(ev.getEventType()))
                .eventDate(ev.getEventDate())
                .eventTime(ev.getEventTime())
                .attendanceStatus(toOption(ev.getAttendanceStatus()))
                .professionName(ev.getProfessionName())
                .professionalUser(toUserDTO(ev.getProfessionalUser()))
                .registeredByUser(toUserDTO(ev.getRegisteredByUser()))
                .program(toProgramDTO(ev.getProgram()))
                .comment(ev.getComment())
                .citationComment(ev.getCitationComment())
                .observation(ev.getObservation())
                .nextAction(ev.getNextAction())
                .nextActionDate(ev.getNextActionDate())
                .resultCode(ev.getResultCode())
                .stateCode(ev.getStateCode())
                .createdAt(ev.getCreatedAt())
                .build();
    }

    private EpisodeReferenceDTO toReferenceDTO(EpisodeReferenceEntity r) {
        if (r == null) return null;
        return EpisodeReferenceDTO.builder()
                .id(r.getId())
                .episodeId(r.getEpisode() != null ? r.getEpisode().getId() : null)
                .originStageId(r.getOriginStage() != null ? r.getOriginStage().getId() : null)
                .destinationStageId(r.getDestinationStage() != null ? r.getDestinationStage().getId() : null)
                .originProgram(toProgramDTO(r.getOriginProgram()))
                .destinationProgram(toProgramDTO(r.getDestinationProgram()))
                .referenceDate(r.getReferenceDate())
                .reason(r.getReason())
                .observation(r.getObservation())
                .createdByUser(toUserDTO(r.getCreatedByUser()))
                .build();
    }

    private EpisodeDocumentDTO toDocumentDTO(EpisodeDocumentEntity d) {
        if (d == null) return null;
        return EpisodeDocumentDTO.builder()
                .id(d.getId())
                .episodeId(d.getEpisode() != null ? d.getEpisode().getId() : null)
                .stageId(d.getStage() != null ? d.getStage().getId() : null)
                .eventId(d.getEvent() != null ? d.getEvent().getId() : null)
                .referenceId(d.getReference() != null ? d.getReference().getId() : null)
                .documentTypeCode(d.getDocumentTypeCode())
                .originalFilename(d.getOriginalFilename())
                .storedFilename(d.getStoredFilename())
                .storagePath(d.getStoragePath())
                .mimeType(d.getMimeType())
                .fileSize(d.getFileSize())
                .uploadedByUser(toUserDTO(d.getUploadedByUser()))
                .uploadedAt(d.getUploadedAt())
                .build();
    }

    private EpisodeAlertDTO toAlertDTO(EpisodeAlertEntity a) {
        if (a == null) return null;
        return EpisodeAlertDTO.builder()
                .id(a.getId())
                .episodeId(a.getEpisode() != null ? a.getEpisode().getId() : null)
                .stageId(a.getStage() != null ? a.getStage().getId() : null)
                .alertTypeCode(a.getAlertTypeCode())
                .priorityLevelCode(a.getPriorityLevelCode())
                .description(a.getDescription())
                .actionTaken(a.getActionTaken())
                .nextAction(a.getNextAction())
                .nextActionDate(a.getNextActionDate())
                .responsibleUser(toUserDTO(a.getResponsibleUser()))
                .statusCode(a.getStatusCode())
                .createdByUser(toUserDTO(a.getCreatedByUser()))
                .createdAt(a.getCreatedAt())
                .build();
    }

    private EpisodeAuditLogDTO toAuditDTO(EpisodeAuditLogEntity a) {
        if (a == null) return null;
        return EpisodeAuditLogDTO.builder()
                .id(a.getId())
                .episodeId(a.getEpisode() != null ? a.getEpisode().getId() : null)
                .stageId(a.getStage() != null ? a.getStage().getId() : null)
                .eventId(a.getEvent() != null ? a.getEvent().getId() : null)
                .actionType(a.getActionType())
                .previousValue(a.getPreviousValue())
                .newValue(a.getNewValue())
                .reason(a.getReason())
                .performedByUser(toUserDTO(a.getPerformedByUser()))
                .authorizedByUser(toUserDTO(a.getAuthorizedByUser()))
                .reversedByUser(toUserDTO(a.getReversedByUser()))
                .performedAt(a.getPerformedAt())
                .reversedAt(a.getReversedAt())
                .build();
    }

    private EpisodeSubstanceDTO toSubstanceDTO(EpisodeSubstanceEntity s) {
        if (s == null) return null;
        return EpisodeSubstanceDTO.builder()
                .id(s.getId())
                .episodeId(s.getEpisode() != null ? s.getEpisode().getId() : null)
                .substanceId(s.getSubstance() != null ? s.getSubstance().getId() : null)
                .substanceName(s.getSubstance() != null ? s.getSubstance().getName() : null)
                .level(s.getLevel())
                .primarySubstance(s.getPrimarySubstance())
                .useOrder(s.getUseOrder())
                .observation(s.getObservation())
                .build();
    }

    private PrioritizedEpisodeDTO toPrioritizedDTO(EpisodeEntity e) {
        int days = accumulatedDays(e);
        List<EpisodeEventEntity> events = eventRepository.findByEpisodeIdOrderByEventDateAscEventTimeAscIdAsc(e.getId());
        EpisodeEventEntity last = events.isEmpty() ? null : events.get(events.size() - 1);
        boolean hasCitation = events.stream().anyMatch(ev -> ev.getEventType() != null && "CITACION".equalsIgnoreCase(ev.getEventType().getCode()));
        return PrioritizedEpisodeDTO.builder()
                .episodeId(e.getId())
                .episodeCode(e.getEpisodeCode())
                .rut(e.getPostulant() != null ? e.getPostulant().getRut() : null)
                .personName(personName(e.getPostulant()))
                .currentProgram(toProgramDTO(e.getCurrentProgram()))
                .originalRequestDate(e.getOriginalRequestDate())
                .accumulatedDays(days)
                .semaphoreColor(resolveSemaphore(days))
                .stateCode(e.getStateCode())
                .resultCode(e.getResultCode())
                .lastManagement(last != null && last.getEventType() != null ? last.getEventType().getName() : null)
                .suggestedAction(suggestedAction(e, hasCitation))
                .build();
    }

    private String personName(PostulantEntity p) {
        if (p == null) return null;
        return String.join(" ", Arrays.asList(
                        Optional.ofNullable(p.getFirstName()).orElse(""),
                        Optional.ofNullable(p.getFirstLastName()).orElse(""),
                        Optional.ofNullable(p.getSecondLastName()).orElse("")))
                .replaceAll("\\s+", " ").trim();
    }

    private String suggestedAction(EpisodeEntity e, boolean hasCitation) {
        if (!hasCitation) return "Registrar primera citación";
        if (RESULT_WAITING_LIST.equalsIgnoreCase(Optional.ofNullable(e.getResultCode()).orElse(""))) return "Gestionar cupo / revisar prioridad";
        if (RESULT_REFERENCE.equalsIgnoreCase(Optional.ofNullable(e.getResultCode()).orElse(""))) return "Confirmar recepción en programa destino";
        if (RESULT_TREATMENT_ENTRY.equalsIgnoreCase(Optional.ofNullable(e.getResultCode()).orElse(""))) return "Registrar egreso cuando corresponda";
        return "Revisar caso y definir resultado";
    }

    private int accumulatedDays(EpisodeEntity e) {
        if (e == null || e.getOriginalRequestDate() == null) return 0;
        LocalDate endDate;
        if (e.getEntryToTreatmentAt() != null) endDate = e.getEntryToTreatmentAt().toLocalDate();
        else if (e.getClosedAt() != null) endDate = e.getClosedAt().toLocalDate();
        else endDate = LocalDate.now();
        long days = ChronoUnit.DAYS.between(e.getOriginalRequestDate(), endDate);
        return (int) Math.max(days, 0);
    }

    private String resolveSemaphore(int days) {
        List<SemaphoreRuleEntity> rules = semaphoreRuleRepository.findByActiveTrueOrderByMinDaysAsc();
        for (SemaphoreRuleEntity rule : rules) {
            int min = rule.getMinDays() == null ? 0 : rule.getMinDays();
            Integer max = rule.getMaxDays();
            if (days >= min && (max == null || days <= max)) return rule.getColorCode();
        }
        if (days <= 30) return "VERDE";
        if (days <= 60) return "AMARILLO";
        return "ROJO";
    }
}
