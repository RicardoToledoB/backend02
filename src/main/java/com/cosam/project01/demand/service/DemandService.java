package com.cosam.project01.demand.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import com.cosam.project01.demand.dto.*;
import com.cosam.project01.demand.entity.*;
import com.cosam.project01.demand.repository.*;
import com.cosam.project01.entity.*;
import com.cosam.project01.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;
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

    private final PostulantRepository postulantRepository;
    private final ProgramRepository programRepository;
    private final ProgramProfessionalRepository programProfessionalRepository;
    private final ContactTypeRepository contactTypeRepository;
    private final SenderRepository senderRepository;
    private final DiverterRepository diverterRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final SubstanceRepository substanceRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.documents.storage-dir:./storage/demand-documents}")
    private String documentStorageDir;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${spring.mail.host:}")
    private String smtpHost;

    @Value("${app.email.from:no-reply@dssm.cl}")
    private String emailFrom;

    @Value("${app.email.from-name:Gestion Demanda DSSM}")
    private String emailFromName;

    @Transactional(readOnly = true)
    public DemandCatalogsDTO getCatalogs() {
        return DemandCatalogsDTO.builder()
                .episodeTypes(episodeTypeRepository.findAll().stream().map(this::toOption).toList())
                .eventTypes(eventTypeRepository.findAll().stream().map(this::toOption).toList())
                .attendanceStatuses(attendanceStatusRepository.findAll().stream().map(this::toOption).toList())
                .citationTypes(citationTypeRepository.findByActiveTrueOrderBySortOrderAscNameAsc().stream().map(this::toOption).toList())
                .biopsychosocialCommitmentLevels(biopsychosocialCommitmentLevelRepository.findByActiveTrueOrderByNameAsc().stream().map(this::toOption).toList())
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
        Integer previousTreatmentNumber = normalizePreviousTreatmentNumber(request.getPreviousTreatmentNumber());

        EpisodeEntity episode = EpisodeEntity.builder()
                .postulant(postulant)
                .episodeType(type)
                .previousTreatmentNumber(previousTreatmentNumber)
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
        Pageable requestedPageable = pageable != null ? pageable : Pageable.unpaged();
        List<PrioritizedEpisodeDTO> rows = new ArrayList<>(episodeRepository
                .findPrioritized(programId, stateCode, resultCode, Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::toPrioritizedDTO)
                .toList());

        applyPrioritizedSort(rows, requestedPageable.getSort());

        if (!requestedPageable.isPaged()) {
            return new PageImpl<>(rows);
        }

        int start = (int) Math.min(requestedPageable.getOffset(), rows.size());
        int end = Math.min(start + requestedPageable.getPageSize(), rows.size());
        return new PageImpl<>(rows.subList(start, end), requestedPageable, rows.size());
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

        List<EpisodeEntity> activeEpisodesForDashboard = episodeRepository
                .findPrioritized(null, null, null, Pageable.unpaged())
                .getContent();
        long withoutFirstCitation = activeEpisodesForDashboard.stream()
                .filter(this::withoutFirstCitationInCurrentStage)
                .count();

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

    @Transactional(readOnly = true)
    public List<DemandSupervisorProgramDTO> getDashboardSupervisorPrograms() {
        List<EpisodeEntity> activeEpisodes = episodeRepository
                .findPrioritized(null, null, null, Pageable.unpaged())
                .getContent();

        Map<Integer, List<EpisodeEntity>> episodesByProgram = activeEpisodes.stream()
                .filter(e -> e.getCurrentProgram() != null && e.getCurrentProgram().getId() != null)
                .collect(Collectors.groupingBy(e -> e.getCurrentProgram().getId(), LinkedHashMap::new, Collectors.toList()));

        Map<Integer, ProgramEntity> programsById = new LinkedHashMap<>();
        programRepository.findAllActive().stream()
                .filter(p -> p.getId() != null)
                .filter(p -> p.getActive() == null || Boolean.TRUE.equals(p.getActive()))
                .sorted(Comparator.comparing(p -> Optional.ofNullable(p.getName()).orElse(""), String.CASE_INSENSITIVE_ORDER))
                .forEach(p -> programsById.put(p.getId(), p));

        activeEpisodes.stream()
                .map(EpisodeEntity::getCurrentProgram)
                .filter(Objects::nonNull)
                .filter(p -> p.getId() != null)
                .forEach(p -> programsById.putIfAbsent(p.getId(), p));

        return programsById.values().stream()
                .map(program -> toSupervisorProgramDashboardDTO(program, episodesByProgram.getOrDefault(program.getId(), List.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DemandSupervisorProgramReferenceDTO> getDashboardSupervisorProgramReferences(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        LocalDate start = from != null ? from : today.withDayOfMonth(1);
        LocalDate end = to != null ? to : (from != null ? today : today.withDayOfMonth(today.lengthOfMonth()));
        if (end.isBefore(start)) {
            throw badRequest("La fecha to no puede ser menor que from.");
        }

        LocalDateTime fromAt = start.atStartOfDay();
        LocalDateTime toExclusive = end.plusDays(1).atStartOfDay();

        List<EpisodeReferenceEntity> referencesInRange = referenceRepository.findAll().stream()
                .filter(ref -> ref.getReferenceDate() != null)
                .filter(ref -> !ref.getReferenceDate().isBefore(fromAt) && ref.getReferenceDate().isBefore(toExclusive))
                .toList();

        Map<Integer, ProgramEntity> programsById = new LinkedHashMap<>();
        programRepository.findAllActive().stream()
                .filter(p -> p.getId() != null)
                .filter(p -> p.getActive() == null || Boolean.TRUE.equals(p.getActive()))
                .sorted(Comparator.comparing(p -> Optional.ofNullable(p.getName()).orElse(""), String.CASE_INSENSITIVE_ORDER))
                .forEach(p -> programsById.put(p.getId(), p));

        referencesInRange.stream()
                .map(EpisodeReferenceEntity::getOriginProgram)
                .filter(Objects::nonNull)
                .filter(p -> p.getId() != null)
                .forEach(p -> programsById.putIfAbsent(p.getId(), p));
        referencesInRange.stream()
                .map(EpisodeReferenceEntity::getDestinationProgram)
                .filter(Objects::nonNull)
                .filter(p -> p.getId() != null)
                .forEach(p -> programsById.putIfAbsent(p.getId(), p));

        Map<Integer, Long> receivedByProgram = new HashMap<>();
        Map<Integer, Long> sentByProgram = new HashMap<>();
        Map<Integer, List<Long>> daysBeforeReferenceByOrigin = new HashMap<>();
        Map<Integer, Map<String, Long>> reasonsByOrigin = new HashMap<>();
        Set<Integer> referencedOriginStageIds = new HashSet<>();

        for (EpisodeReferenceEntity ref : referencesInRange) {
            Integer originProgramId = ref.getOriginProgram() != null ? ref.getOriginProgram().getId() : null;
            Integer destinationProgramId = ref.getDestinationProgram() != null ? ref.getDestinationProgram().getId() : null;

            if (originProgramId != null) {
                sentByProgram.merge(originProgramId, 1L, Long::sum);
                long daysBeforeReference = daysBetween(ref.getOriginStage() != null ? ref.getOriginStage().getReceivedAt() : null, ref.getReferenceDate());
                daysBeforeReferenceByOrigin.computeIfAbsent(originProgramId, k -> new ArrayList<>()).add(daysBeforeReference);
                String reason = hasText(ref.getReason()) ? ref.getReason().trim() : "Sin motivo informado";
                reasonsByOrigin.computeIfAbsent(originProgramId, k -> new LinkedHashMap<>()).merge(reason, 1L, Long::sum);
            }
            if (destinationProgramId != null) {
                receivedByProgram.merge(destinationProgramId, 1L, Long::sum);
            }
            if (ref.getOriginStage() != null && ref.getOriginStage().getId() != null) {
                referencedOriginStageIds.add(ref.getOriginStage().getId());
            }
        }

        Map<Integer, Long> pendingReferencesByProgram = new HashMap<>();
        episodeRepository.findPrioritized(null, null, null, Pageable.unpaged()).getContent().forEach(episode -> {
            EpisodeStageEntity currentStage = resolveCurrentStageForRead(episode);
            if (currentStage == null || currentStage.getProgram() == null || currentStage.getProgram().getId() == null) return;
            if (!RESULT_REFERENCE.equalsIgnoreCase(Optional.ofNullable(currentStage.getResultCode()).orElse(""))) return;
            if (currentStage.getId() != null && referencedOriginStageIds.contains(currentStage.getId())) return;
            if (!isStageWithinRange(currentStage, start, end)) return;
            pendingReferencesByProgram.merge(currentStage.getProgram().getId(), 1L, Long::sum);
            programsById.putIfAbsent(currentStage.getProgram().getId(), currentStage.getProgram());
        });

        return programsById.values().stream()
                .map(program -> toProgramReferenceStatsDTO(
                        program,
                        receivedByProgram.getOrDefault(program.getId(), 0L),
                        sentByProgram.getOrDefault(program.getId(), 0L),
                        pendingReferencesByProgram.getOrDefault(program.getId(), 0L),
                        daysBeforeReferenceByOrigin.getOrDefault(program.getId(), List.of()),
                        reasonsByOrigin.getOrDefault(program.getId(), Map.of())
                ))
                .toList();
    }

    @Transactional
    public EpisodeEventDTO createEvent(Integer episodeId, CreateEventRequest request) {
        EpisodeEntity episode = findOpenEpisode(episodeId);
        EpisodeStageEntity stage = resolveStage(episode, request.getStageId());
        EventTypeEntity type = resolveEventType(request.getEventTypeId(), request.getEventTypeCode());
        AttendanceStatusEntity attendance = resolveAttendanceStatus(request.getAttendanceStatusId(), request.getAttendanceStatusCode(), null);
        boolean isRetroalimentacion = "RETROALIMENTACION".equalsIgnoreCase(type.getCode());
        BiopsychosocialCommitmentLevelEntity biopsychosocialCommitmentLevel = isRetroalimentacion
                ? resolveBiopsychosocialCommitmentLevel(request.getBiopsychosocialCommitmentCode())
                : null;
        UserEntity currentUser = currentUserOrNull();
        UserEntity professional = findNullable(userRepository, request.getProfessionalUserId());
        ProgramProfessionalEntity programProfessional = findNullableProgramProfessional(request.getProgramProfessionalId());
        String professionName = firstText(request.getProfessionName(), professionNameFromProgramProfessional(programProfessional));
        ProgramEntity program = request.getProgramId() != null
                ? programRepository.findById(request.getProgramId()).orElseThrow(() -> notFound("Programa no encontrado"))
                : stage.getProgram();
        EpisodeEventEntity relatedEvent = resolveRelatedEvent(episode, request.getRelatedEventId());

        if (isRetroalimentacion) {
            validateRetroalimentacionRequest(request, professional, programProfessional, professionName);
        }

        EpisodeEventEntity event = EpisodeEventEntity.builder()
                .episode(episode)
                .stage(stage)
                .eventType(type)
                .relatedEvent(relatedEvent)
                .biopsychosocialCommitmentLevel(biopsychosocialCommitmentLevel)
                .eventDate(request.getEventDate())
                .eventTime(request.getEventTime())
                .attendanceStatus(attendance)
                .professionName(professionName)
                .professionalUser(professional)
                .programProfessional(programProfessional)
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

        if (isRetroalimentacion && RESULT_TREATMENT_ENTRY.equalsIgnoreCase(request.getResultCode())) {
            LocalDateTime entryAt = LocalDateTime.of(request.getEventDate(), request.getEventTime());
            episode.setEntryToTreatmentAt(entryAt);
            episode.setWaitingStopped(true);
            stage.setResultCode(RESULT_TREATMENT_ENTRY);
            stageRepository.save(stage);
        }

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
        CitationTypeEntity citationType = resolveCitationType(request.getCitationTypeCode());
        UserEntity professional = findNullable(userRepository, request.getProfessionalUserId());
        ProgramProfessionalEntity programProfessional = findNullableProgramProfessional(request.getProgramProfessionalId());
        String professionName = firstText(request.getProfessionName(), professionNameFromProgramProfessional(programProfessional));
        UserEntity currentUser = currentUserOrNull();
        ProgramEntity program = request.getProgramId() != null
                ? programRepository.findById(request.getProgramId()).orElseThrow(() -> notFound("Programa no encontrado"))
                : stage.getProgram();

        EpisodeEventEntity event = EpisodeEventEntity.builder()
                .episode(episode)
                .stage(stage)
                .eventType(eventType("CITACION"))
                .citationType(citationType)
                .eventDate(request.getCitationDate())
                .eventTime(request.getCitationTime())
                .attendanceStatus(attendanceStatus("AGENDADO"))
                .professionName(professionName)
                .professionalUser(professional)
                .programProfessional(programProfessional)
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
        ProgramProfessionalEntity programProfessional = findNullableProgramProfessional(request.getProgramProfessionalId());
        String professionName = firstText(request.getProfessionName(), professionNameFromProgramProfessional(programProfessional));
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
                .professionName(professionName)
                .professionalUser(professional)
                .programProfessional(programProfessional)
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
            long noShows;
            if (programProfessional != null) {
                noShows = eventRepository.countNoShowByStageAndProgramProfessional(stage.getId(), programProfessional.getId());
            } else {
                Integer professionalUserId = professional != null ? professional.getId() : null;
                noShows = eventRepository.countNoShowByStageAndProfessional(stage.getId(), professionalUserId);
            }
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
        EpisodeEntity episode = findOpenEpisode(episodeId);
        EpisodeStageEntity stage = resolveStage(episode, null);
        UserEntity currentUser = currentUserOrNull();
        boolean explicitClosureReason = request.getClosureReasonId() != null || hasText(request.getClosureReasonCode());
        ClosureReasonEntity reason = resolveClosureReason(request.getClosureReasonId(), request.getClosureReasonCode());
        LocalDateTime closureAt = parseOptionalDateTime(request.getClosureDate());

        if (explicitClosureReason && reason.getCode() != null && reason.getCode().toUpperCase().contains("OTRO") && !hasText(request.getClosureComment())) {
            throw badRequest("Cuando la causal es OTRO, la observación/comentario de cierre es obligatoria.");
        }

        closeEpisodeInternal(episode, stage, reason, request.getClosureComment(), currentUser, RESULT_CLOSURE, closureAt);
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
        EpisodeDocumentEntity document = request.getDocumentId() != null
                ? documentRepository.findById(request.getDocumentId()).orElseThrow(() -> notFound("Documento no encontrado"))
                : null;
        if (episode == null && document != null) episode = document.getEpisode();

        audit(episode, episode != null ? episode.getCurrentStage() : null, null, "SOLICITUD_ENVIO_CORREO", null, request.getTo(), request.getSubject(), currentUserOrNull(), null, null);

        Integer resolvedEpisodeId = episode != null ? episode.getId() : request.getEpisodeId();
        Integer resolvedDocumentId = document != null ? document.getId() : request.getDocumentId();
        List<Integer> requestedDocumentIds = normalizeDocumentIds(resolvedDocumentId, request.getDocumentIds());

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (!emailEnabled || !hasText(smtpHost) || mailSender == null) {
            return EmailNotificationResponse.builder()
                    .sent(false)
                    .queued(false)
                    .result("EMAIL_SERVICE_NOT_CONFIGURED")
                    .message("SMTP no configurado. Defina SMTP_HOST y, si corresponde, SMTP_USERNAME/SMTP_PASSWORD en el servicio systemd. El endpoint ya usa envio real cuando la configuracion existe.")
                    .episodeId(resolvedEpisodeId)
                    .documentId(resolvedDocumentId)
                    .to(request.getTo())
                    .subject(request.getSubject())
                    .documentIds(requestedDocumentIds)
                    .sentAt(LocalDateTime.now())
                    .build();
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(emailFrom, emailFromName);
            helper.setTo(splitRecipients(request.getTo()));
            if (hasText(request.getCc())) helper.setCc(splitRecipients(request.getCc()));
            if (hasText(request.getBcc())) helper.setBcc(splitRecipients(request.getBcc()));
            helper.setSubject(request.getSubject());
            helper.setText(request.getMessage(), false);

            for (Integer documentId : requestedDocumentIds) {
                EpisodeDocumentEntity attachment = documentRepository.findById(documentId)
                        .orElseThrow(() -> notFound("Documento no encontrado: " + documentId));
                Path attachmentPath = resolveAttachmentPath(attachment);
                helper.addAttachment(attachmentFilename(attachment), new FileSystemResource(attachmentPath));
            }

            mailSender.send(mimeMessage);

            audit(episode, episode != null ? episode.getCurrentStage() : null, null, "ENVIO_CORREO_REALIZADO", null, request.getTo(), request.getSubject(), currentUserOrNull(), null, null);
            return EmailNotificationResponse.builder()
                    .sent(true)
                    .queued(false)
                    .result("EMAIL_SENT")
                    .message("Correo enviado correctamente.")
                    .episodeId(resolvedEpisodeId)
                    .documentId(resolvedDocumentId)
                    .to(request.getTo())
                    .subject(request.getSubject())
                    .documentIds(requestedDocumentIds)
                    .sentAt(LocalDateTime.now())
                    .build();
        } catch (MessagingException | java.io.UnsupportedEncodingException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No fue posible preparar el correo: " + ex.getMessage());
        } catch (org.springframework.mail.MailException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No fue posible enviar el correo SMTP: " + ex.getMessage());
        }
    }

    private List<Integer> normalizeDocumentIds(Integer documentId, List<Integer> documentIds) {
        LinkedHashSet<Integer> ids = new LinkedHashSet<>();
        if (documentId != null) ids.add(documentId);
        if (documentIds != null) {
            documentIds.stream().filter(Objects::nonNull).forEach(ids::add);
        }
        return new ArrayList<>(ids);
    }

    private String[] splitRecipients(String recipients) {
        if (!hasText(recipients)) {
            throw badRequest("Debe indicar al menos un destinatario.");
        }
        return Arrays.stream(recipients.split("[;,]"))
                .map(String::trim)
                .filter(this::hasText)
                .toArray(String[]::new);
    }

    private Path resolveAttachmentPath(EpisodeDocumentEntity document) {
        if (!hasText(document.getStoragePath())) {
            throw badRequest("El documento " + document.getId() + " no tiene ruta fisica registrada.");
        }
        Path path = Paths.get(document.getStoragePath()).toAbsolutePath().normalize();
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo fisico no encontrado para documento " + document.getId());
        }
        return path;
    }

    private String attachmentFilename(EpisodeDocumentEntity document) {
        if (hasText(document.getOriginalFilename())) return cleanFilename(document.getOriginalFilename());
        if (hasText(document.getStoredFilename())) return cleanFilename(document.getStoredFilename());
        return "documento-" + document.getId();
    }

    @Transactional(readOnly = true)
    public List<EpisodeSubstanceDTO> listSubstances(Integer episodeId) {
        findEpisode(episodeId);
        return orderedSubstances(episodeId).stream()
                .map(this::toSubstanceDTO)
                .toList();
    }

    @Transactional
    public EpisodeSubstanceDTO addSubstance(Integer episodeId, CreateSubstanceRequest request) {
        EpisodeEntity episode = findEpisode(episodeId);
        SubstanceEntity substance = substanceRepository.findById(request.getSubstanceId())
                .orElseThrow(() -> notFound("Sustancia no encontrada"));

        ensureSubstanceNotDuplicated(episodeId, substance.getId(), null);

        boolean primary = resolvePrimaryForCreate(episodeId, request.getPrimarySubstance(), request.getLevel(), request.getUseOrder());
        Integer useOrder = resolveUseOrderForCreate(episodeId, primary, request.getUseOrder(), request.getLevel());

        if (primary) {
            demoteOtherPrimarySubstances(episodeId, null);
        }

        EpisodeSubstanceEntity entity = EpisodeSubstanceEntity.builder()
                .episode(episode)
                .substance(substance)
                .level(request.getLevel())
                .primarySubstance(primary)
                .useOrder(useOrder)
                .observation(request.getObservation())
                .build();
        entity = episodeSubstanceRepository.save(entity);
        audit(episode, episode.getCurrentStage(), null, "REGISTRAR_SUSTANCIA", null, substance.getName(), request.getObservation(), currentUserOrNull(), null, null);
        return toSubstanceDTO(entity);
    }

    @Transactional
    public EpisodeSubstanceDTO updateSubstance(Integer episodeId, Integer substanceAssociationId, UpdateSubstanceRequest request) {
        EpisodeEntity episode = findEpisode(episodeId);
        EpisodeSubstanceEntity entity = findEpisodeSubstance(episodeId, substanceAssociationId);

        SubstanceEntity previousSubstance = entity.getSubstance();
        if (request.getSubstanceId() != null) {
            SubstanceEntity substance = substanceRepository.findById(request.getSubstanceId())
                    .orElseThrow(() -> notFound("Sustancia no encontrada"));
            ensureSubstanceNotDuplicated(episodeId, substance.getId(), entity.getId());
            entity.setSubstance(substance);
        }

        if (request.getLevel() != null) {
            entity.setLevel(request.getLevel());
        }
        if (request.getUseOrder() != null) {
            validateUseOrder(request.getUseOrder());
            entity.setUseOrder(request.getUseOrder());
        } else if (entity.getUseOrder() == null) {
            entity.setUseOrder(nextUseOrder(episodeId));
        }

        Boolean requestedPrimary = request.getPrimarySubstance();
        if (requestedPrimary != null) {
            if (Boolean.TRUE.equals(requestedPrimary)) {
                demoteOtherPrimarySubstances(episodeId, entity.getId());
                entity.setPrimarySubstance(true);
                if (entity.getUseOrder() == null) entity.setUseOrder(1);
            } else {
                if (isOnlyPrimarySubstance(episodeId, entity.getId())) {
                    throw badRequest("Debe existir una sustancia principal para el episodio. Marque otra sustancia como principal antes de desmarcar esta.");
                }
                entity.setPrimarySubstance(false);
            }
        }

        if (request.getObservation() != null) {
            entity.setObservation(request.getObservation());
        }

        entity = episodeSubstanceRepository.save(entity);
        audit(episode, episode.getCurrentStage(), null, "MODIFICAR_SUSTANCIA",
                previousSubstance != null ? previousSubstance.getName() : null,
                entity.getSubstance() != null ? entity.getSubstance().getName() : null,
                request.getObservation(), currentUserOrNull(), null, null);
        return toSubstanceDTO(entity);
    }

    @Transactional
    public void deleteSubstance(Integer episodeId, Integer substanceAssociationId) {
        EpisodeEntity episode = findEpisode(episodeId);
        EpisodeSubstanceEntity entity = findEpisodeSubstance(episodeId, substanceAssociationId);
        boolean wasPrimary = Boolean.TRUE.equals(entity.getPrimarySubstance());
        String substanceName = entity.getSubstance() != null ? entity.getSubstance().getName() : null;

        episodeSubstanceRepository.delete(entity);
        episodeSubstanceRepository.flush();

        if (wasPrimary) {
            promoteFirstRemainingSubstanceAsPrimary(episodeId);
        }

        audit(episode, episode.getCurrentStage(), null, "ELIMINAR_SUSTANCIA", substanceName, null, null, currentUserOrNull(), null, null);
    }

    private EpisodeSubstanceEntity findEpisodeSubstance(Integer episodeId, Integer substanceAssociationId) {
        EpisodeSubstanceEntity entity = episodeSubstanceRepository.findById(substanceAssociationId)
                .orElseThrow(() -> notFound("Sustancia asociada al episodio no encontrada"));
        if (entity.getEpisode() == null || !Objects.equals(entity.getEpisode().getId(), episodeId)) {
            throw badRequest("La sustancia indicada no pertenece al episodio solicitado.");
        }
        return entity;
    }

    private List<EpisodeSubstanceEntity> orderedSubstances(Integer episodeId) {
        return episodeSubstanceRepository.findByEpisodeId(episodeId).stream()
                .sorted(Comparator
                        .comparing((EpisodeSubstanceEntity s) -> !Boolean.TRUE.equals(s.getPrimarySubstance()))
                        .thenComparing(s -> s.getUseOrder() != null ? s.getUseOrder() : Integer.MAX_VALUE)
                        .thenComparing(EpisodeSubstanceEntity::getId))
                .toList();
    }

    private void ensureSubstanceNotDuplicated(Integer episodeId, Integer substanceId, Integer currentAssociationId) {
        if (substanceId == null) {
            throw badRequest("Debe indicar substanceId.");
        }
        boolean exists = currentAssociationId == null
                ? episodeSubstanceRepository.findByEpisodeIdAndSubstanceId(episodeId, substanceId).isPresent()
                : episodeSubstanceRepository.findByEpisodeIdAndSubstanceIdAndIdNot(episodeId, substanceId, currentAssociationId).isPresent();
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La sustancia ya se encuentra asociada al episodio.");
        }
    }

    private boolean resolvePrimaryForCreate(Integer episodeId, Boolean requestedPrimary, String level, Integer useOrder) {
        if (requestedPrimary != null) return requestedPrimary;
        if (episodeSubstanceRepository.findByEpisodeId(episodeId).isEmpty()) return true;
        return Boolean.TRUE.equals(isPrimaryLevel(level, useOrder));
    }

    private Integer resolveUseOrderForCreate(Integer episodeId, boolean primary, Integer requestedUseOrder, String level) {
        Integer useOrder = requestedUseOrder != null ? requestedUseOrder : parseLevelAsOrder(level);
        if (useOrder == null) useOrder = primary ? 1 : nextUseOrder(episodeId);
        validateUseOrder(useOrder);
        return useOrder;
    }

    private void validateUseOrder(Integer useOrder) {
        if (useOrder == null || useOrder < 1) {
            throw badRequest("useOrder debe ser un entero mayor o igual a 1.");
        }
    }

    private Integer nextUseOrder(Integer episodeId) {
        return episodeSubstanceRepository.findByEpisodeId(episodeId).stream()
                .map(EpisodeSubstanceEntity::getUseOrder)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .map(v -> v + 1)
                .orElse(1);
    }

    private void demoteOtherPrimarySubstances(Integer episodeId, Integer keepAssociationId) {
        List<EpisodeSubstanceEntity> primaries = episodeSubstanceRepository.findByEpisodeIdAndPrimarySubstanceTrue(episodeId);
        for (EpisodeSubstanceEntity item : primaries) {
            if (keepAssociationId == null || !Objects.equals(item.getId(), keepAssociationId)) {
                item.setPrimarySubstance(false);
                if (item.getUseOrder() == null || item.getUseOrder() == 1) {
                    item.setUseOrder(nextUseOrder(episodeId));
                }
                episodeSubstanceRepository.save(item);
            }
        }
    }

    private boolean isOnlyPrimarySubstance(Integer episodeId, Integer associationId) {
        return episodeSubstanceRepository.findByEpisodeIdAndPrimarySubstanceTrue(episodeId).stream()
                .allMatch(s -> Objects.equals(s.getId(), associationId));
    }

    private void promoteFirstRemainingSubstanceAsPrimary(Integer episodeId) {
        List<EpisodeSubstanceEntity> remaining = orderedSubstances(episodeId);
        if (remaining.isEmpty()) return;
        boolean hasPrimary = remaining.stream().anyMatch(s -> Boolean.TRUE.equals(s.getPrimarySubstance()));
        if (hasPrimary) return;
        EpisodeSubstanceEntity first = remaining.get(0);
        first.setPrimarySubstance(true);
        if (first.getUseOrder() == null) first.setUseOrder(1);
        episodeSubstanceRepository.save(first);
    }

    private DemandSupervisorProgramDTO toSupervisorProgramDashboardDTO(ProgramEntity program, List<EpisodeEntity> episodes) {
        List<EpisodeEntity> safeEpisodes = episodes != null ? episodes : List.of();
        long activeDemands = safeEpisodes.size();
        long totalDays = 0;
        long redCases = 0;
        long withoutFirstCitation = 0;
        long withoutFeedback = 0;
        long severeCommitmentCases = 0;
        long pendingReferences = 0;
        long pendingClosures = 0;
        long openAlerts = 0;

        for (EpisodeEntity episode : safeEpisodes) {
            int days = accumulatedDays(episode);
            totalDays += days;
            if ("ROJO".equalsIgnoreCase(resolveSemaphore(days))) {
                redCases++;
            }

            List<EpisodeEventEntity> events = eventRepository.findByEpisodeIdOrderByEventDateAscEventTimeAscIdAsc(episode.getId());
            List<EpisodeEventEntity> currentStageEvents = filterEventsByStage(events, resolveCurrentStageForRead(episode));
            if (!hasFirstCitationForDashboard(currentStageEvents)) {
                withoutFirstCitation++;
            }
            if (!hasEventType(events, "RETROALIMENTACION")) {
                withoutFeedback++;
            }

            EpisodeEventEntity feedback = latestEventByType(events, "RETROALIMENTACION");
            String commitmentCode = feedback != null && feedback.getBiopsychosocialCommitmentLevel() != null
                    ? feedback.getBiopsychosocialCommitmentLevel().getCode()
                    : null;
            if ("SEVERO".equalsIgnoreCase(commitmentCode)) {
                severeCommitmentCases++;
            }

            if (isPendingReference(episode)) {
                pendingReferences++;
            }
            if (isPendingClosure(episode)) {
                pendingClosures++;
            }

            openAlerts += alertRepository.findByEpisodeIdOrderByCreatedAtDesc(episode.getId()).stream()
                    .filter(this::isOpenAlert)
                    .count();
        }

        double averageDays = activeDemands > 0 ? (double) totalDays / activeDemands : 0.0;

        return DemandSupervisorProgramDTO.builder()
                .programId(program != null ? program.getId() : null)
                .programName(program != null ? program.getName() : null)
                .activeDemands(activeDemands)
                .averageAccumulatedDays(Math.round(averageDays * 10.0) / 10.0)
                .redCases(redCases)
                .withoutFirstCitation(withoutFirstCitation)
                .withoutFeedback(withoutFeedback)
                .severeCommitmentCases(severeCommitmentCases)
                .pendingReferences(pendingReferences)
                .pendingClosures(pendingClosures)
                .openAlerts(openAlerts)
                .build();
    }

    private DemandSupervisorProgramReferenceDTO toProgramReferenceStatsDTO(
            ProgramEntity program,
            long receivedReferences,
            long sentReferences,
            long pendingReferences,
            List<Long> daysBeforeReference,
            Map<String, Long> referenceReasons) {
        double averageDays = daysBeforeReference == null || daysBeforeReference.isEmpty()
                ? 0.0
                : daysBeforeReference.stream().filter(Objects::nonNull).mapToLong(Long::longValue).average().orElse(0.0);

        List<ReferenceReasonDTO> reasons = referenceReasons == null ? List.of() : referenceReasons.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)))
                .map(entry -> ReferenceReasonDTO.builder()
                        .reason(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .toList();

        return DemandSupervisorProgramReferenceDTO.builder()
                .programId(program != null ? program.getId() : null)
                .programName(program != null ? program.getName() : null)
                .receivedReferences(receivedReferences)
                .sentReferences(sentReferences)
                .pendingReferences(pendingReferences)
                .referenceBalance(receivedReferences - sentReferences)
                .averageDaysBeforeReference(Math.round(averageDays * 10.0) / 10.0)
                .referenceReasons(reasons)
                .build();
    }

    private long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0L;
        long days = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
        return Math.max(days, 0L);
    }

    private boolean isStageWithinRange(EpisodeStageEntity stage, LocalDate from, LocalDate to) {
        if (stage == null || stage.getReceivedAt() == null) return true;
        LocalDate date = stage.getReceivedAt().toLocalDate();
        return (date.isEqual(from) || date.isAfter(from)) && (date.isEqual(to) || date.isBefore(to));
    }

    private boolean hasFirstCitationForDashboard(List<EpisodeEventEntity> events) {
        if (events == null || events.isEmpty()) return false;
        return events.stream().anyMatch(this::isFirstCitationFirstInterview);
    }

    private boolean withoutFirstCitationInCurrentStage(EpisodeEntity episode) {
        if (episode == null || episode.getId() == null) return true;
        List<EpisodeEventEntity> events = eventRepository.findByEpisodeIdOrderByEventDateAscEventTimeAscIdAsc(episode.getId());
        return !hasFirstCitationForDashboard(filterEventsByStage(events, resolveCurrentStageForRead(episode)));
    }

    private List<EpisodeEventEntity> filterEventsByStage(List<EpisodeEventEntity> events, EpisodeStageEntity stage) {
        if (events == null || events.isEmpty() || stage == null || stage.getId() == null) {
            return List.of();
        }
        Integer stageId = stage.getId();
        return events.stream()
                .filter(ev -> ev.getStage() != null && Objects.equals(ev.getStage().getId(), stageId))
                .toList();
    }

    private boolean isFirstCitationFirstInterview(EpisodeEventEntity event) {
        if (event == null || event.getEventType() == null || !"CITACION".equalsIgnoreCase(event.getEventType().getCode())) {
            return false;
        }
        String citationTypeCode = event.getCitationType() != null ? normalizeCode(event.getCitationType().getCode()) : null;
        return "PRIMERA_CITACION_PRIMERA_ENTREVISTA".equals(citationTypeCode)
                || "FIRST_CITATION_FIRST_INTERVIEW".equals(citationTypeCode);
    }

    private boolean hasEventType(List<EpisodeEventEntity> events, String eventTypeCode) {
        if (events == null || !hasText(eventTypeCode)) return false;
        return events.stream().anyMatch(ev -> ev.getEventType() != null
                && eventTypeCode.equalsIgnoreCase(ev.getEventType().getCode()));
    }

    private boolean isPendingReference(EpisodeEntity episode) {
        return episode != null
                && Boolean.TRUE.equals(episode.getActive())
                && episode.getClosedAt() == null
                && RESULT_REFERENCE.equalsIgnoreCase(Optional.ofNullable(episode.getResultCode()).orElse(""));
    }

    private boolean isPendingClosure(EpisodeEntity episode) {
        if (episode == null || !Boolean.TRUE.equals(episode.getActive()) || episode.getClosedAt() != null) return false;
        return episode.getEntryToTreatmentAt() != null
                || RESULT_TREATMENT_ENTRY.equalsIgnoreCase(Optional.ofNullable(episode.getResultCode()).orElse(""));
    }

    private boolean isOpenAlert(EpisodeAlertEntity alert) {
        if (alert == null) return false;
        String status = normalizeCode(alert.getStatusCode());
        return "ABIERTA".equals(status) || "ACTIVA".equals(status) || "OPEN".equals(status);
    }

    private Integer normalizePreviousTreatmentNumber(Integer value) {
        if (value == null) return 0;
        if (value < 0) throw badRequest("El número de tratamientos previos debe ser mayor o igual a 0.");
        return value;
    }

    private EpisodeLongitudinalDTO buildLongitudinal(List<EpisodeEntity> episodes) {
        EpisodeEntity first = episodes.get(0);
        List<Integer> episodeIds = episodes.stream().map(EpisodeEntity::getId).toList();
        List<EpisodeStageDTO> stages = new ArrayList<>();
        List<EpisodeEventDTO> events = new ArrayList<>();
        List<EpisodeReferenceDTO> references = new ArrayList<>();
        List<EpisodeAlertDTO> alerts = new ArrayList<>();
        long openAlertCount = 0;
        List<EpisodeDocumentDTO> documents = new ArrayList<>();
        List<EpisodeAuditLogDTO> auditLogs = new ArrayList<>();

        for (Integer id : episodeIds) {
            stages.addAll(stageRepository.findByEpisodeIdOrderByStageOrderAsc(id).stream().map(this::toStageDTO).toList());
            events.addAll(eventRepository.findByEpisodeIdOrderByEventDateAscEventTimeAscIdAsc(id).stream().map(this::toEventDTO).toList());
            references.addAll(referenceRepository.findByEpisodeIdOrderByReferenceDateAsc(id).stream().map(this::toReferenceDTO).toList());
            List<EpisodeAlertEntity> episodeAlerts = alertRepository.findByEpisodeIdOrderByCreatedAtDesc(id);
            openAlertCount += episodeAlerts.stream().filter(this::isOpenAlert).count();
            alerts.addAll(episodeAlerts.stream().map(this::toAlertDTO).toList());
            documents.addAll(documentRepository.findByEpisodeIdOrderByUploadedAtDesc(id).stream().map(this::toDocumentDTO).toList());
            auditLogs.addAll(auditLogRepository.findByEpisodeIdOrderByPerformedAtDesc(id).stream().map(this::toAuditDTO).toList());
        }

        return EpisodeLongitudinalDTO.builder()
                .openAlertCount(openAlertCount)
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

    private EpisodeStageEntity resolveCurrentStageForRead(EpisodeEntity episode) {
        if (episode == null) return null;
        if (episode.getCurrentStage() != null) return episode.getCurrentStage();
        return stageRepository.findFirstByEpisodeIdAndCurrentTrueOrderByStageOrderDesc(episode.getId()).orElse(null);
    }

    private Integer daysInStage(EpisodeStageEntity stage) {
        if (stage == null || stage.getReceivedAt() == null) return 0;
        LocalDate endDate = (stage.getClosedAt() != null ? stage.getClosedAt() : LocalDateTime.now()).toLocalDate();
        long days = ChronoUnit.DAYS.between(stage.getReceivedAt().toLocalDate(), endDate);
        return (int) Math.max(days, 0);
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
        closeEpisodeInternal(episode, stage, reason, comment, currentUser, resultCode, null);
    }

    private void closeEpisodeInternal(EpisodeEntity episode, EpisodeStageEntity stage, ClosureReasonEntity reason, String comment, UserEntity currentUser, String resultCode, LocalDateTime closureDate) {
        LocalDateTime now = closureDate != null ? closureDate : LocalDateTime.now();
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

    private CitationTypeEntity resolveCitationType(String code) {
        if (!hasText(code)) return null;
        CitationTypeEntity citationType = citationTypeRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> notFound("Tipo de citación no encontrado: " + code));
        if (!Boolean.TRUE.equals(citationType.getActive())) {
            throw badRequest("Tipo de citación inactivo: " + code);
        }
        return citationType;
    }

    private BiopsychosocialCommitmentLevelEntity resolveBiopsychosocialCommitmentLevel(String code) {
        if (!hasText(code)) {
            throw badRequest("Debe indicar biopsychosocialCommitmentCode para eventos RETROALIMENTACION");
        }
        BiopsychosocialCommitmentLevelEntity level = biopsychosocialCommitmentLevelRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> notFound("Nivel de compromiso biopsicosocial no encontrado: " + code));
        if (!Boolean.TRUE.equals(level.getActive())) {
            throw badRequest("Nivel de compromiso biopsicosocial inactivo: " + code);
        }
        return level;
    }

    private void validateRetroalimentacionRequest(CreateEventRequest request, UserEntity professional,
                                                   ProgramProfessionalEntity programProfessional, String professionName) {
        if (request.getEventDate() == null) {
            throw badRequest("La fecha es obligatoria para eventos RETROALIMENTACION");
        }
        if (request.getEventTime() == null) {
            throw badRequest("La hora es obligatoria para eventos RETROALIMENTACION");
        }
        if (professional == null && programProfessional == null && !hasText(professionName)) {
            throw badRequest("Debe indicar profesional para eventos RETROALIMENTACION");
        }
        if (!hasText(request.getResultCode())) {
            throw badRequest("Debe indicar resultCode para eventos RETROALIMENTACION");
        }
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

    private LocalDateTime parseOptionalDateTime(String value) {
        if (!hasText(value)) return null;
        String trimmed = value.trim();
        try {
            return LocalDateTime.parse(trimmed);
        } catch (DateTimeParseException ignored) {
            // Se intenta como fecha simple YYYY-MM-DD.
        }
        try {
            return LocalDate.parse(trimmed).atStartOfDay();
        } catch (DateTimeParseException ex) {
            throw badRequest("closureDate debe tener formato YYYY-MM-DD o YYYY-MM-DDTHH:mm:ss");
        }
    }

    private AttendanceStatusEntity attendanceStatus(String code) {
        return attendanceStatusRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> notFound("Estado de asistencia no encontrado: " + code));
    }

    private ClosureReasonEntity resolveClosureReason(Integer id, String code) {
        if (id != null) return closureReasonRepository.findById(id).orElseThrow(() -> notFound("Motivo de cierre no encontrado"));
        if (hasText(code)) return closureReason(code);
        return closureReasonRepository.findByCodeIgnoreCase("OTRO_CIERRE")
                .or(() -> closureReasonRepository.findByCodeIgnoreCase("ABANDONO"))
                .orElseThrow(() -> badRequest("Debe existir al menos un motivo de cierre activo para cerrar el episodio"));
    }

    private ClosureReasonEntity closureReason(String code) {
        return closureReasonRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> notFound("Motivo de cierre no encontrado: " + code));
    }

    private ProgramProfessionalEntity findNullableProgramProfessional(Long id) {
        if (id == null) return null;
        return programProfessionalRepository.findById(id)
                .orElseThrow(() -> notFound("Facultativo no encontrado: " + id));
    }

    private String professionNameFromProgramProfessional(ProgramProfessionalEntity professional) {
        if (professional == null || professional.getProfession() == null) return null;
        return professional.getProfession().getName();
    }

    private String firstText(String preferred, String fallback) {
        return hasText(preferred) ? preferred : fallback;
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
    private OptionDTO toOption(CitationTypeEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(BiopsychosocialCommitmentLevelEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(ClosureReasonEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(ProgramPopulationEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(ProgramModalityEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(ProgramPlanEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(DocumentTypeEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(RegionEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(CityEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(e.getCode()).name(e.getName()).build(); }
    private OptionDTO toOption(ContactTypeEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(null).name(e.getName()).build(); }
    private OptionDTO toOption(SenderEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(null).name(e.getName()).build(); }
    private OptionDTO toOption(DiverterEntity e) { return e == null ? null : OptionDTO.builder().id(e.getId()).code(null).name(e.getName()).build(); }

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

    private String userDisplayName(UserEntity user) {
        UserSummaryDTO summary = toUserDTO(user);
        return summary != null ? summary.getName() : null;
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
                .previousTreatmentNumber(e.getPreviousTreatmentNumber() != null ? e.getPreviousTreatmentNumber() : 0)
                .originalRequestDate(e.getOriginalRequestDate())
                .initialProgram(toProgramDTO(e.getInitialProgram()))
                .currentProgram(toProgramDTO(e.getCurrentProgram()))
                .contactType(toOption(e.getContactType()))
                .sender(toOption(e.getSender()))
                .diverter(toOption(e.getDiverter()))
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
                .citationType(toOption(ev.getCitationType()))
                .biopsychosocialCommitmentLevel(toOption(ev.getBiopsychosocialCommitmentLevel()))
                .eventDate(ev.getEventDate())
                .eventTime(ev.getEventTime())
                .attendanceStatus(toOption(ev.getAttendanceStatus()))
                .professionName(ev.getProfessionName())
                .professionalUser(toUserDTO(ev.getProfessionalUser()))
                .programProfessionalId(ev.getProgramProfessional() != null ? ev.getProgramProfessional().getId() : null)
                .programProfessionalName(ev.getProgramProfessional() != null ? ev.getProgramProfessional().getName() : null)
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
                .type(a.getAlertTypeCode())
                .priority(a.getPriorityLevelCode())
                .status(a.getStatusCode())
                .nextReviewDate(a.getNextActionDate())
                .responsibleUserName(userDisplayName(a.getResponsibleUser()))
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
        EpisodeEventEntity feedback = latestEventByType(events, "RETROALIMENTACION");
        EpisodeStageEntity currentStage = resolveCurrentStageForRead(e);
        EpisodeStageEntity originStage = currentStage != null ? currentStage.getOriginStage() : null;
        ProgramEntity originProgram = originStage != null ? originStage.getProgram() : null;
        String currentStageStateCode = currentStage != null ? currentStage.getStateCode() : e.getStateCode();
        String currentStageResultCode = currentStage != null ? currentStage.getResultCode() : e.getResultCode();
        Integer currentStageId = currentStage != null ? currentStage.getId() : null;
        List<EpisodeEventEntity> currentStageEvents = filterEventsByStage(events, currentStage);
        EpisodeEventEntity last = latestEvent(currentStageEvents);
        boolean hasFirstCitationInCurrentStage = hasFirstCitationForDashboard(currentStageEvents);
        boolean hasFeedbackInCurrentStage = currentStageEvents.stream().anyMatch(ev ->
                ev.getEventType() != null
                        && "RETROALIMENTACION".equalsIgnoreCase(ev.getEventType().getCode()));

        return PrioritizedEpisodeDTO.builder()
                .episodeId(e.getId())
                .episodeCode(e.getEpisodeCode())
                .rut(e.getPostulant() != null ? e.getPostulant().getRut() : null)
                .personName(personName(e.getPostulant()))
                .currentProgram(toProgramDTO(e.getCurrentProgram()))
                .currentStageId(currentStageId)
                .currentStageStateCode(currentStageStateCode)
                .currentStageResultCode(currentStageResultCode)
                .currentStageReceivedAt(currentStage != null ? currentStage.getReceivedAt() : null)
                .currentStageDays(daysInStage(currentStage))
                .originProgramId(originProgram != null ? originProgram.getId() : null)
                .originProgramName(originProgram != null ? originProgram.getName() : null)
                .referenceCount(referenceRepository.countByEpisodeId(e.getId()))
                .originalRequestDate(e.getOriginalRequestDate())
                .accumulatedDays(days)
                .semaphoreColor(resolveSemaphore(days))
                .stateCode(currentStageStateCode)
                .resultCode(currentStageResultCode)
                .lastManagement(last != null && last.getEventType() != null ? last.getEventType().getName() : null)
                .lastManagementDate(last != null ? last.getEventDate() : null)
                .lastManagementTime(last != null ? last.getEventTime() : null)
                .firstCitationFirstInterviewDate(latestCitationDateByType(currentStageEvents, "PRIMERA_CITACION_PRIMERA_ENTREVISTA", "FIRST_CITATION_FIRST_INTERVIEW"))
                .secondCitationFirstInterviewDate(latestCitationDateByType(currentStageEvents, "SEGUNDA_CITACION_PRIMERA_ENTREVISTA", "SECOND_CITATION_FIRST_INTERVIEW"))
                .firstCitationSecondInterviewDate(latestCitationDateByType(currentStageEvents, "PRIMERA_CITACION_SEGUNDA_ENTREVISTA", "FIRST_CITATION_SECOND_INTERVIEW"))
                .secondCitationSecondInterviewDate(latestCitationDateByType(currentStageEvents, "SEGUNDA_CITACION_SEGUNDA_ENTREVISTA", "SECOND_CITATION_SECOND_INTERVIEW"))
                .firstCitationThirdInterviewDate(latestCitationDateByType(currentStageEvents, "PRIMERA_CITACION_TERCERA_ENTREVISTA", "FIRST_CITATION_THIRD_INTERVIEW"))
                .secondCitationThirdInterviewDate(latestCitationDateByType(currentStageEvents, "SEGUNDA_CITACION_TERCERA_ENTREVISTA", "SECOND_CITATION_THIRD_INTERVIEW"))
                .optionalInterviewDate(latestCitationDateByType(currentStageEvents, "ENTREVISTA_OPCIONAL", "OPTIONAL_INTERVIEW"))
                .feedbackDate(feedback != null ? feedback.getEventDate() : null)
                .closureDate(e.getClosedAt() != null ? e.getClosedAt().toLocalDate() : null)
                .biopsychosocialCommitmentCode(feedback != null && feedback.getBiopsychosocialCommitmentLevel() != null
                        ? feedback.getBiopsychosocialCommitmentLevel().getCode()
                        : null)
                .suggestedAction(suggestedAction(e, currentStage, hasFirstCitationInCurrentStage, hasFeedbackInCurrentStage))
                .build();
    }

    private EpisodeEventEntity latestEvent(List<EpisodeEventEntity> events) {
        if (events == null || events.isEmpty()) return null;
        return events.get(events.size() - 1);
    }

    private EpisodeEventEntity latestEventByType(List<EpisodeEventEntity> events, String eventTypeCode) {
        if (events == null || !hasText(eventTypeCode)) return null;
        for (int i = events.size() - 1; i >= 0; i--) {
            EpisodeEventEntity event = events.get(i);
            if (event.getEventType() != null && eventTypeCode.equalsIgnoreCase(event.getEventType().getCode())) {
                return event;
            }
        }
        return null;
    }

    private LocalDate latestCitationDateByType(List<EpisodeEventEntity> events, String... citationTypeCodes) {
        EpisodeEventEntity citation = latestCitationByType(events, citationTypeCodes);
        return citation != null ? citation.getEventDate() : null;
    }

    private void applyPrioritizedSort(List<PrioritizedEpisodeDTO> rows, Sort sort) {
        if (rows == null || rows.size() < 2 || sort == null || sort.isUnsorted()) return;

        Comparator<PrioritizedEpisodeDTO> comparator = null;
        for (Sort.Order order : sort) {
            Comparator<PrioritizedEpisodeDTO> fieldComparator = (left, right) ->
                    comparePrioritizedField(left, right, order.getProperty(), order.isAscending());
            comparator = comparator == null ? fieldComparator : comparator.thenComparing(fieldComparator);
        }

        if (comparator != null) {
            rows.sort(comparator.thenComparing(PrioritizedEpisodeDTO::getEpisodeId, Comparator.nullsLast(Integer::compareTo)));
        }
    }

    private int comparePrioritizedField(PrioritizedEpisodeDTO left, PrioritizedEpisodeDTO right, String property, boolean ascending) {
        String key = Optional.ofNullable(property).orElse("episodeId")
                .replace("_", "")
                .replace(".", "")
                .toLowerCase(Locale.ROOT);

        if ("biopsychosocialcommitmentcode".equals(key) || "biopsychosocialcommitmentlevel".equals(key)) {
            return compareBiopsychosocialCommitment(left.getBiopsychosocialCommitmentCode(), right.getBiopsychosocialCommitmentCode(), ascending);
        }

        return compareNullableComparable(prioritizedSortValue(left, key), prioritizedSortValue(right, key), ascending);
    }

    private Comparable<?> prioritizedSortValue(PrioritizedEpisodeDTO dto, String key) {
        if (dto == null) return null;
        return switch (key) {
            case "episodeid", "id" -> dto.getEpisodeId();
            case "episodecode" -> normalizeSortText(dto.getEpisodeCode());
            case "rut" -> normalizeSortText(dto.getRut());
            case "personname", "name", "patientname" -> normalizeSortText(dto.getPersonName());
            case "currentprogram", "currentprogramname", "program", "programname" ->
                    dto.getCurrentProgram() != null ? normalizeSortText(dto.getCurrentProgram().getName()) : null;
            case "currentprogramid", "programid" -> dto.getCurrentProgram() != null ? dto.getCurrentProgram().getId() : null;
            case "currentstageid" -> dto.getCurrentStageId();
            case "currentstagestatecode" -> normalizeSortText(dto.getCurrentStageStateCode());
            case "currentstageresultcode" -> normalizeSortText(dto.getCurrentStageResultCode());
            case "currentstagereceivedat" -> dto.getCurrentStageReceivedAt();
            case "currentstagedays" -> dto.getCurrentStageDays();
            case "originprogramid" -> dto.getOriginProgramId();
            case "originprogramname" -> normalizeSortText(dto.getOriginProgramName());
            case "referencecount" -> dto.getReferenceCount();
            case "originalrequestdate" -> dto.getOriginalRequestDate();
            case "accumulateddays" -> dto.getAccumulatedDays();
            case "semaphorecolor" -> normalizeSortText(dto.getSemaphoreColor());
            case "statecode" -> normalizeSortText(dto.getStateCode());
            case "resultcode" -> normalizeSortText(dto.getResultCode());
            case "lastmanagement" -> normalizeSortText(dto.getLastManagement());
            case "lastmanagementdate" -> dto.getLastManagementDate();
            case "lastmanagementtime" -> dto.getLastManagementTime();
            case "firstcitationfirstinterviewdate" -> dto.getFirstCitationFirstInterviewDate();
            case "secondcitationfirstinterviewdate" -> dto.getSecondCitationFirstInterviewDate();
            case "firstcitationsecondinterviewdate" -> dto.getFirstCitationSecondInterviewDate();
            case "secondcitationsecondinterviewdate" -> dto.getSecondCitationSecondInterviewDate();
            case "firstcitationthirdinterviewdate" -> dto.getFirstCitationThirdInterviewDate();
            case "secondcitationthirdinterviewdate" -> dto.getSecondCitationThirdInterviewDate();
            case "optionalinterviewdate" -> dto.getOptionalInterviewDate();
            case "feedbackdate" -> dto.getFeedbackDate();
            case "closuredate" -> dto.getClosureDate();
            case "suggestedaction" -> normalizeSortText(dto.getSuggestedAction());
            default -> dto.getEpisodeId();
        };
    }

    private String normalizeSortText(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int compareNullableComparable(Comparable left, Comparable right, boolean ascending) {
        if (left == null && right == null) return 0;
        if (left == null) return 1;
        if (right == null) return -1;
        int result = left.compareTo(right);
        return ascending ? result : -result;
    }

    private int compareBiopsychosocialCommitment(String left, String right, boolean ascending) {
        Integer leftRank = biopsychosocialCommitmentRank(left);
        Integer rightRank = biopsychosocialCommitmentRank(right);
        if (leftRank == null && rightRank == null) return 0;
        if (leftRank == null) return 1;
        if (rightRank == null) return -1;
        return ascending ? Integer.compare(leftRank, rightRank) : Integer.compare(rightRank, leftRank);
    }

    private Integer biopsychosocialCommitmentRank(String code) {
        if (!hasText(code)) return null;
        return switch (code.trim().toUpperCase(Locale.ROOT)) {
            case "SEVERO" -> 1;
            case "MODERADO" -> 2;
            case "LEVE" -> 3;
            default -> 4;
        };
    }

    private String personName(PostulantEntity p) {
        if (p == null) return null;
        return String.join(" ", Arrays.asList(
                        Optional.ofNullable(p.getFirstName()).orElse(""),
                        Optional.ofNullable(p.getFirstLastName()).orElse(""),
                        Optional.ofNullable(p.getSecondLastName()).orElse("")))
                .replaceAll("\\s+", " ").trim();
    }

    private String suggestedAction(EpisodeEntity e, EpisodeStageEntity currentStage, boolean hasCitation, boolean hasFeedbackInCurrentStage) {
        String currentResultCode = currentStage != null ? currentStage.getResultCode() : (e != null ? e.getResultCode() : null);
        List<EpisodeEventEntity> currentStageEvents = List.of();
        if (e != null && e.getId() != null) {
            currentStageEvents = filterEventsByStage(
                    eventRepository.findByEpisodeIdOrderByEventDateAscEventTimeAscIdAsc(e.getId()),
                    currentStage
            );
        }

        String workflowAction = suggestedInterviewWorkflowAction(currentStageEvents);
        if (workflowAction != null) {
            return workflowAction;
        }

        if (RESULT_WAITING_LIST.equalsIgnoreCase(Optional.ofNullable(currentResultCode).orElse(""))) return "Gestionar cupo / revisar prioridad";
        if (RESULT_REFERENCE.equalsIgnoreCase(Optional.ofNullable(currentResultCode).orElse(""))) return "Confirmar recepción en programa destino";
        if (RESULT_TREATMENT_ENTRY.equalsIgnoreCase(Optional.ofNullable(currentResultCode).orElse(""))) return "Registrar cierre cuando corresponda";
        if (!hasFeedbackInCurrentStage) return "Registrar retroalimentación";
        return "Revisar caso y definir resultado";
    }

    private String suggestedInterviewWorkflowAction(List<EpisodeEventEntity> currentStageEvents) {
        EpisodeEventEntity firstInterviewFirstCitation = latestCitationByType(currentStageEvents,
                "PRIMERA_CITACION_PRIMERA_ENTREVISTA", "FIRST_CITATION_FIRST_INTERVIEW");
        EpisodeEventEntity firstInterviewSecondCitation = latestCitationByType(currentStageEvents,
                "SEGUNDA_CITACION_PRIMERA_ENTREVISTA", "SECOND_CITATION_FIRST_INTERVIEW");
        EpisodeEventEntity secondInterviewFirstCitation = latestCitationByType(currentStageEvents,
                "PRIMERA_CITACION_SEGUNDA_ENTREVISTA", "FIRST_CITATION_SECOND_INTERVIEW");
        EpisodeEventEntity secondInterviewSecondCitation = latestCitationByType(currentStageEvents,
                "SEGUNDA_CITACION_SEGUNDA_ENTREVISTA", "SECOND_CITATION_SECOND_INTERVIEW");
        EpisodeEventEntity thirdInterviewFirstCitation = latestCitationByType(currentStageEvents,
                "PRIMERA_CITACION_TERCERA_ENTREVISTA", "FIRST_CITATION_THIRD_INTERVIEW");
        EpisodeEventEntity thirdInterviewSecondCitation = latestCitationByType(currentStageEvents,
                "SEGUNDA_CITACION_TERCERA_ENTREVISTA", "SECOND_CITATION_THIRD_INTERVIEW");

        if (!isInterviewCompleted(currentStageEvents, firstInterviewFirstCitation, firstInterviewSecondCitation)) {
            if (firstInterviewFirstCitation == null) {
                return "Programar primera citación a primera entrevista";
            }
            if (isCitationReprogrammingRequired(currentStageEvents, firstInterviewFirstCitation)) {
                return "Reprogramar primera citación a primera entrevista";
            }
            if (isCitationNoShow(currentStageEvents, firstInterviewFirstCitation) && firstInterviewSecondCitation == null) {
                return "Programar segunda citación a primera entrevista";
            }
            if (isCitationReprogrammingRequired(currentStageEvents, firstInterviewSecondCitation)) {
                return "Reprogramar segunda citación a primera entrevista";
            }
            if (isCitationPendingAttendance(currentStageEvents, firstInterviewFirstCitation, firstInterviewSecondCitation)) {
                return "Registrar asistencia de primera entrevista";
            }
            return "Revisar caso y definir resultado";
        }

        if (!isInterviewCompleted(currentStageEvents, secondInterviewFirstCitation, secondInterviewSecondCitation)) {
            if (secondInterviewFirstCitation == null) {
                return "Programar primera citación a segunda entrevista";
            }
            if (isCitationReprogrammingRequired(currentStageEvents, secondInterviewFirstCitation)) {
                return "Reprogramar primera citación a segunda entrevista";
            }
            if (isCitationNoShow(currentStageEvents, secondInterviewFirstCitation) && secondInterviewSecondCitation == null) {
                return "Programar segunda citación a segunda entrevista";
            }
            if (isCitationReprogrammingRequired(currentStageEvents, secondInterviewSecondCitation)) {
                return "Reprogramar segunda citación a segunda entrevista";
            }
            if (isCitationPendingAttendance(currentStageEvents, secondInterviewFirstCitation, secondInterviewSecondCitation)) {
                return "Registrar asistencia de segunda entrevista";
            }
            return "Revisar caso y definir resultado";
        }

        if (!isInterviewCompleted(currentStageEvents, thirdInterviewFirstCitation, thirdInterviewSecondCitation)) {
            if (thirdInterviewFirstCitation == null) {
                return "Programar primera citación a tercera entrevista";
            }
            if (isCitationReprogrammingRequired(currentStageEvents, thirdInterviewFirstCitation)) {
                return "Reprogramar primera citación a tercera entrevista";
            }
            if (isCitationNoShow(currentStageEvents, thirdInterviewFirstCitation) && thirdInterviewSecondCitation == null) {
                return "Programar segunda citación a tercera entrevista";
            }
            if (isCitationReprogrammingRequired(currentStageEvents, thirdInterviewSecondCitation)) {
                return "Reprogramar segunda citación a tercera entrevista";
            }
            if (isCitationPendingAttendance(currentStageEvents, thirdInterviewFirstCitation, thirdInterviewSecondCitation)) {
                return "Registrar asistencia de tercera entrevista";
            }
            return "Revisar caso y definir resultado";
        }

        if (!hasEventType(currentStageEvents, "RETROALIMENTACION")) {
            return "Registrar retroalimentación";
        }
        return null;
    }

    private EpisodeEventEntity latestCitationByType(List<EpisodeEventEntity> events, String... citationTypeCodes) {
        if (events == null || events.isEmpty() || citationTypeCodes == null || citationTypeCodes.length == 0) return null;
        Set<String> allowedCodes = Arrays.stream(citationTypeCodes)
                .filter(this::hasText)
                .map(this::normalizeCode)
                .collect(Collectors.toSet());
        if (allowedCodes.isEmpty()) return null;
        for (int i = events.size() - 1; i >= 0; i--) {
            EpisodeEventEntity event = events.get(i);
            if (event == null || event.getEventType() == null || !"CITACION".equalsIgnoreCase(event.getEventType().getCode())) {
                continue;
            }
            String eventCitationTypeCode = event.getCitationType() != null ? normalizeCode(event.getCitationType().getCode()) : null;
            if (eventCitationTypeCode != null && allowedCodes.contains(eventCitationTypeCode)) {
                return event;
            }
        }
        return null;
    }

    private boolean isInterviewCompleted(List<EpisodeEventEntity> events, EpisodeEventEntity firstCitation, EpisodeEventEntity secondCitation) {
        return isCitationPresented(events, firstCitation) || isCitationPresented(events, secondCitation);
    }

    private boolean isCitationPresented(List<EpisodeEventEntity> events, EpisodeEventEntity citation) {
        return hasCitationAttendanceStatus(events, citation, "SE_PRESENTO");
    }

    private boolean isCitationNoShow(List<EpisodeEventEntity> events, EpisodeEventEntity citation) {
        return hasCitationAttendanceStatus(events, citation, "NO_SE_PRESENTO");
    }

    private boolean isCitationReprogrammingRequired(List<EpisodeEventEntity> events, EpisodeEventEntity citation) {
        String statusCode = latestAttendanceStatusCodeForCitation(events, citation);
        return "CANCELA_PROGRAMA".equals(statusCode) || "REPROGRAMADA".equals(statusCode);
    }

    private boolean isCitationPendingAttendance(List<EpisodeEventEntity> events, EpisodeEventEntity... citations) {
        if (citations == null || citations.length == 0) return false;
        for (EpisodeEventEntity citation : citations) {
            if (citation == null) continue;
            String statusCode = latestAttendanceStatusCodeForCitation(events, citation);
            if ("CANCELA_PROGRAMA".equals(statusCode) || "REPROGRAMADA".equals(statusCode)) {
                continue;
            }
            if (!"SE_PRESENTO".equals(statusCode) && !"NO_SE_PRESENTO".equals(statusCode)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCitationAttendanceStatus(List<EpisodeEventEntity> events, EpisodeEventEntity citation, String attendanceStatusCode) {
        if (citation == null || !hasText(attendanceStatusCode)) return false;
        String expected = normalizeCode(attendanceStatusCode);
        return expected.equals(latestAttendanceStatusCodeForCitation(events, citation));
    }

    private String latestAttendanceStatusCodeForCitation(List<EpisodeEventEntity> events, EpisodeEventEntity citation) {
        if (citation == null) return null;
        Integer citationId = citation.getId();
        if (citationId != null && events != null && !events.isEmpty()) {
            for (int i = events.size() - 1; i >= 0; i--) {
                EpisodeEventEntity event = events.get(i);
                if (event != null
                        && event.getEventType() != null
                        && "ASISTENCIA".equalsIgnoreCase(event.getEventType().getCode())
                        && event.getRelatedEvent() != null
                        && Objects.equals(event.getRelatedEvent().getId(), citationId)
                        && event.getAttendanceStatus() != null) {
                    return normalizeCode(event.getAttendanceStatus().getCode());
                }
            }
        }
        return citation.getAttendanceStatus() != null ? normalizeCode(citation.getAttendanceStatus().getCode()) : null;
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
