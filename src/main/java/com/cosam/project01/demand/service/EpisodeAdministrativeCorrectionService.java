package com.cosam.project01.demand.service;

import com.cosam.project01.demand.dto.*;
import com.cosam.project01.demand.entity.*;
import com.cosam.project01.demand.repository.*;
import com.cosam.project01.entity.*;
import com.cosam.project01.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EpisodeAdministrativeCorrectionService {

    private static final String ACTION_CREATE = "CREATE";
    private static final String ACTION_UPDATE = "UPDATE";
    private static final String ACTION_DELETE = "DELETE";
    private static final String STATE_IN_PROGRESS = "EN_TRAMITE";
    private static final String STATE_CLOSED = "CERRADO";
    private static final String RESULT_PENDING = "AUN_SIN_RESULTADO";
    private static final String RESULT_TREATMENT_ENTRY = "INGRESO_TRATAMIENTO";

    private final EpisodeRepository episodeRepository;
    private final EpisodeStageRepository stageRepository;
    private final EpisodeEventRepository eventRepository;
    private final EpisodeSubstanceRepository episodeSubstanceRepository;
    private final EpisodeAuditLogRepository auditLogRepository;
    private final EpisodeTypeRepository episodeTypeRepository;
    private final EventTypeRepository eventTypeRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;
    private final CitationTypeRepository citationTypeRepository;
    private final BiopsychosocialCommitmentLevelRepository biopsychosocialCommitmentLevelRepository;
    private final ClosureReasonRepository closureReasonRepository;

    private final ProgramRepository programRepository;
    private final ProgramProfessionalRepository programProfessionalRepository;
    private final ContactTypeRepository contactTypeRepository;
    private final SenderRepository senderRepository;
    private final DiverterRepository diverterRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final SubstanceRepository substanceRepository;

    @Transactional
    public AdministrativeCorrectionResponse correctEpisode(Integer episodeId,
                                                            AdministrativeCorrectionRequest request,
                                                            String performedByEmail) {
        if (request == null) {
            throw badRequest("Debe enviar el cuerpo de la corrección administrativa.");
        }
        if (!hasText(request.getCorrectionReason())) {
            throw badRequest("Debe indicar correctionReason para registrar la auditoría de la corrección.");
        }

        EpisodeEntity episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> notFound("Episodio no encontrado"));
        EpisodeStageEntity targetStage = resolveTargetStage(episode, request.getStageId(), request.getProgramId());
        UserEntity currentUser = currentUser(performedByEmail);

        AdministrativeCorrectionResponse response = AdministrativeCorrectionResponse.builder()
                .episodeId(episode.getId())
                .episodeCode(episode.getEpisodeCode())
                .programId(targetStage != null && targetStage.getProgram() != null ? targetStage.getProgram().getId() : request.getProgramId())
                .programName(targetStage != null && targetStage.getProgram() != null ? targetStage.getProgram().getName() : null)
                .stageId(targetStage != null ? targetStage.getId() : null)
                .correctionReason(request.getCorrectionReason())
                .performedBy(performedByEmail)
                .performedAt(LocalDateTime.now())
                .episodeUpdated(false)
                .closureUpdated(false)
                .auditRecords(0)
                .build();

        if (request.getEpisode() != null) {
            String before = snapshotEpisode(episode);
            boolean changed = applyEpisodePatch(episode, request.getEpisode());
            if (changed) {
                episode = episodeRepository.save(episode);
                String after = snapshotEpisode(episode);
                audit(episode, targetStage, null, "CORRECCION_ADMINISTRATIVA_EPISODIO", before, after, request.getCorrectionReason(), currentUser);
                response.setEpisodeUpdated(true);
                inc(response, "episodeUpdated");
                incAudit(response);
            }
        }

        if (request.getClosure() != null) {
            EpisodeStageEntity closureStage = resolveTargetStage(episode,
                    firstNonNull(request.getClosure().getStageId(), request.getStageId()),
                    request.getProgramId());
            String beforeStage = snapshotStage(closureStage);
            boolean closureChanged = applyStageClosurePatch(episode, closureStage, request.getClosure(), currentUser);
            if (closureChanged) {
                String afterStage = snapshotStage(closureStage);
                audit(episode, closureStage, null, "CORRECCION_ADMINISTRATIVA_CIERRE_ETAPA", beforeStage, afterStage, request.getCorrectionReason(), currentUser);
                response.setClosureUpdated(true);
                inc(response, "closureUpdated");
                incAudit(response);
            }
        }

        for (AdministrativeSubstanceCorrectionDTO item : safeList(request.getSubstances())) {
            applySubstanceCorrection(episode, targetStage, item, request.getCorrectionReason(), currentUser, response);
        }

        for (AdministrativeEventCorrectionDTO item : safeList(request.getEvents())) {
            applyEventCorrection(episode, targetStage, item, null, request.getCorrectionReason(), currentUser, response);
        }
        for (AdministrativeEventCorrectionDTO item : safeList(request.getCitations())) {
            applyEventCorrection(episode, targetStage, item, "CITACION", request.getCorrectionReason(), currentUser, response);
        }
        for (AdministrativeEventCorrectionDTO item : safeList(request.getAttendances())) {
            applyEventCorrection(episode, targetStage, item, "ASISTENCIA", request.getCorrectionReason(), currentUser, response);
        }
        for (AdministrativeEventCorrectionDTO item : safeList(request.getFeedbacks())) {
            applyEventCorrection(episode, targetStage, item, "RETROALIMENTACION", request.getCorrectionReason(), currentUser, response);
        }
        for (AdministrativeEventCorrectionDTO item : safeList(request.getObservations())) {
            applyEventCorrection(episode, targetStage, item, "OBSERVACION", request.getCorrectionReason(), currentUser, response);
        }

        response.setProgramId(targetStage != null && targetStage.getProgram() != null ? targetStage.getProgram().getId() : response.getProgramId());
        response.setProgramName(targetStage != null && targetStage.getProgram() != null ? targetStage.getProgram().getName() : response.getProgramName());
        response.setStageId(targetStage != null ? targetStage.getId() : response.getStageId());
        return response;
    }

    private boolean applyEpisodePatch(EpisodeEntity episode, AdministrativeEpisodeCorrectionDTO patch) {
        boolean changed = false;
        if (patch.getEpisodeTypeId() != null || hasText(patch.getEpisodeTypeCode())) {
            episode.setEpisodeType(resolveEpisodeType(patch.getEpisodeTypeId(), patch.getEpisodeTypeCode()));
            changed = true;
        }
        if (patch.getPreviousTreatmentNumber() != null) {
            if (patch.getPreviousTreatmentNumber() < 0) throw badRequest("previousTreatmentNumber debe ser mayor o igual a 0.");
            episode.setPreviousTreatmentNumber(patch.getPreviousTreatmentNumber());
            changed = true;
        }
        if (patch.getOriginalRequestDate() != null) { episode.setOriginalRequestDate(patch.getOriginalRequestDate()); changed = true; }
        if (patch.getInitialProgramId() != null) { episode.setInitialProgram(program(patch.getInitialProgramId())); changed = true; }
        if (patch.getCurrentProgramId() != null) { episode.setCurrentProgram(program(patch.getCurrentProgramId())); changed = true; }
        if (patch.getCurrentStageId() != null) {
            EpisodeStageEntity stage = stageRepository.findById(patch.getCurrentStageId()).orElseThrow(() -> notFound("Etapa actual no encontrada"));
            ensureStageBelongsToEpisode(episode, stage);
            episode.setCurrentStage(stage);
            if (stage.getProgram() != null) episode.setCurrentProgram(stage.getProgram());
            changed = true;
        }
        if (patch.getContactTypeId() != null) { episode.setContactType(contactTypeRepository.findById(patch.getContactTypeId()).orElseThrow(() -> notFound("Tipo de contacto no encontrado"))); changed = true; }
        if (patch.getSenderId() != null) { episode.setSender(senderRepository.findById(patch.getSenderId()).orElseThrow(() -> notFound("Derivador/origen no encontrado"))); changed = true; }
        if (patch.getDiverterId() != null) { episode.setDiverter(diverterRepository.findById(patch.getDiverterId()).orElseThrow(() -> notFound("Vía de derivación no encontrada"))); changed = true; }
        if (patch.getContactId() != null) { episode.setContact(contactRepository.findById(patch.getContactId()).orElseThrow(() -> notFound("Contacto no encontrado"))); changed = true; }
        if (hasText(patch.getStateCode())) { episode.setStateCode(patch.getStateCode().trim()); changed = true; }
        if (hasText(patch.getResultCode())) { episode.setResultCode(patch.getResultCode().trim()); changed = true; }
        if (patch.getEntryToTreatmentAt() != null) { episode.setEntryToTreatmentAt(patch.getEntryToTreatmentAt()); changed = true; }
        if (patch.getEgressAt() != null) { episode.setEgressAt(patch.getEgressAt()); changed = true; }
        if (patch.getClosedAt() != null) { episode.setClosedAt(patch.getClosedAt()); changed = true; }
        if (patch.getClosureReasonId() != null || hasText(patch.getClosureReasonCode())) { episode.setClosureReason(resolveClosureReason(patch.getClosureReasonId(), patch.getClosureReasonCode())); changed = true; }
        if (patch.getClosureComment() != null) { episode.setClosureComment(patch.getClosureComment()); changed = true; }
        if (patch.getActive() != null) { episode.setActive(patch.getActive()); changed = true; }
        if (patch.getWaitingStopped() != null) { episode.setWaitingStopped(patch.getWaitingStopped()); changed = true; }
        return changed;
    }

    private boolean applyStageClosurePatch(EpisodeEntity episode, EpisodeStageEntity stage,
                                           AdministrativeStageClosureCorrectionDTO patch,
                                           UserEntity currentUser) {
        if (stage == null) throw badRequest("Debe indicar stageId o programId para corregir el cierre de etapa.");
        boolean changed = false;
        LocalDateTime closedAt = firstNonNull(patch.getClosedAt(), parseOptionalDateTime(patch.getClosureDate()));
        ClosureReasonEntity reason = (patch.getClosureReasonId() != null || hasText(patch.getClosureReasonCode()))
                ? resolveClosureReason(patch.getClosureReasonId(), patch.getClosureReasonCode())
                : null;
        String comment = firstText(patch.getClosureComment(), patch.getObservation(), patch.getComment());
        boolean markClosed = Boolean.TRUE.equals(patch.getClosed()) || closedAt != null || reason != null;

        if (markClosed) {
            LocalDateTime effectiveClosedAt = closedAt != null ? closedAt : LocalDateTime.now();
            stage.setClosedAt(effectiveClosedAt);
            stage.setStateCode(STATE_CLOSED);
            if (reason != null) stage.setClosureReason(reason);
            if (hasText(patch.getResultCode())) stage.setResultCode(patch.getResultCode().trim());
            else if (reason != null && hasText(reason.getCode())) stage.setResultCode(reason.getCode());
            if (comment != null) stage.setClosureComment(comment);
            changed = true;

            if (Boolean.TRUE.equals(patch.getCloseEpisode())) {
                episode.setClosedAt(effectiveClosedAt);
                episode.setStateCode(STATE_CLOSED);
                episode.setResultCode(stage.getResultCode());
                episode.setClosureReason(stage.getClosureReason());
                episode.setClosureComment(stage.getClosureComment());
                episode.setClosedByUser(currentUser);
                episode.setActive(false);
                episodeRepository.save(episode);
            }
        } else {
            if (hasText(patch.getStateCode())) { stage.setStateCode(patch.getStateCode().trim()); changed = true; }
            if (hasText(patch.getResultCode())) { stage.setResultCode(patch.getResultCode().trim()); changed = true; }
            if (comment != null) { stage.setClosureComment(comment); changed = true; }
        }
        if (changed) stageRepository.save(stage);
        return changed;
    }

    private void applySubstanceCorrection(EpisodeEntity episode, EpisodeStageEntity stage,
                                          AdministrativeSubstanceCorrectionDTO item,
                                          String correctionReason,
                                          UserEntity currentUser,
                                          AdministrativeCorrectionResponse response) {
        if (item == null) return;
        String action = resolveAction(item.getAction(), firstNonNull(item.getSubstanceAssociationId(), item.getId()));
        if (ACTION_DELETE.equals(action)) {
            Integer id = firstNonNull(item.getSubstanceAssociationId(), item.getId());
            if (id == null) throw badRequest("Para eliminar sustancia debe indicar id o substanceAssociationId.");
            EpisodeSubstanceEntity entity = findEpisodeSubstance(episode.getId(), id);
            String before = snapshotSubstance(entity);
            boolean wasPrimary = Boolean.TRUE.equals(entity.getPrimarySubstance());
            episodeSubstanceRepository.delete(entity);
            episodeSubstanceRepository.flush();
            if (wasPrimary) promoteFirstRemainingSubstanceAsPrimary(episode.getId());
            audit(episode, stage, null, "CORRECCION_ADMINISTRATIVA_ELIMINAR_SUSTANCIA", before, null, correctionReason, currentUser);
            inc(response, "deletedSubstances");
            incAudit(response);
            return;
        }

        if (ACTION_CREATE.equals(action)) {
            if (item.getSubstanceId() == null) throw badRequest("Para crear sustancia debe indicar substanceId.");
            ensureSubstanceNotDuplicated(episode.getId(), item.getSubstanceId(), null);
            SubstanceEntity substance = substance(item.getSubstanceId());
            boolean primary = item.getPrimarySubstance() != null ? item.getPrimarySubstance() : episodeSubstanceRepository.findByEpisodeId(episode.getId()).isEmpty();
            Integer useOrder = item.getUseOrder() != null ? item.getUseOrder() : (primary ? 1 : nextUseOrder(episode.getId()));
            validateUseOrder(useOrder);
            if (primary) demoteOtherPrimarySubstances(episode.getId(), null);
            EpisodeSubstanceEntity entity = EpisodeSubstanceEntity.builder()
                    .episode(episode)
                    .substance(substance)
                    .level(item.getLevel())
                    .primarySubstance(primary)
                    .useOrder(useOrder)
                    .observation(item.getObservation())
                    .build();
            entity = episodeSubstanceRepository.save(entity);
            audit(episode, stage, null, "CORRECCION_ADMINISTRATIVA_CREAR_SUSTANCIA", null, snapshotSubstance(entity), correctionReason, currentUser);
            inc(response, "createdSubstances");
            incAudit(response);
            return;
        }

        Integer id = firstNonNull(item.getSubstanceAssociationId(), item.getId());
        if (id == null) throw badRequest("Para actualizar sustancia debe indicar id o substanceAssociationId.");
        EpisodeSubstanceEntity entity = findEpisodeSubstance(episode.getId(), id);
        String before = snapshotSubstance(entity);
        if (item.getSubstanceId() != null) {
            ensureSubstanceNotDuplicated(episode.getId(), item.getSubstanceId(), entity.getId());
            entity.setSubstance(substance(item.getSubstanceId()));
        }
        if (item.getLevel() != null) entity.setLevel(item.getLevel());
        if (item.getUseOrder() != null) { validateUseOrder(item.getUseOrder()); entity.setUseOrder(item.getUseOrder()); }
        if (item.getPrimarySubstance() != null) {
            if (Boolean.TRUE.equals(item.getPrimarySubstance())) demoteOtherPrimarySubstances(episode.getId(), entity.getId());
            entity.setPrimarySubstance(item.getPrimarySubstance());
        }
        if (item.getObservation() != null) entity.setObservation(item.getObservation());
        entity = episodeSubstanceRepository.save(entity);
        audit(episode, stage, null, "CORRECCION_ADMINISTRATIVA_ACTUALIZAR_SUSTANCIA", before, snapshotSubstance(entity), correctionReason, currentUser);
        inc(response, "updatedSubstances");
        incAudit(response);
    }

    private void applyEventCorrection(EpisodeEntity episode, EpisodeStageEntity defaultStage,
                                      AdministrativeEventCorrectionDTO item,
                                      String defaultEventTypeCode,
                                      String correctionReason,
                                      UserEntity currentUser,
                                      AdministrativeCorrectionResponse response) {
        if (item == null) return;
        Integer eventId = firstNonNull(item.getEventId(), item.getId());
        String action = resolveAction(item.getAction(), eventId);
        if (ACTION_DELETE.equals(action)) {
            if (eventId == null) throw badRequest("Para eliminar evento debe indicar id o eventId.");
            EpisodeEventEntity event = findEpisodeEvent(episode.getId(), eventId);
            String before = snapshotEvent(event);
            EpisodeEventEntity relatedCitation = event.getRelatedEvent();
            eventRepository.delete(event);
            eventRepository.flush();
            if (relatedCitation != null) recomputeCitationAttendance(episode.getId(), relatedCitation);
            audit(episode, event.getStage() != null ? event.getStage() : defaultStage, null,
                    "CORRECCION_ADMINISTRATIVA_ELIMINAR_EVENTO", before, null, correctionReason, currentUser);
            inc(response, "deletedEvents");
            incAudit(response);
            return;
        }

        if (ACTION_CREATE.equals(action)) {
            EpisodeStageEntity stage = item.getStageId() != null ? resolveStage(episode, item.getStageId()) : defaultStage;
            if (stage == null) throw badRequest("Debe indicar stageId o programId para crear eventos.");
            EpisodeEventEntity event = new EpisodeEventEntity();
            event.setEpisode(episode);
            event.setStage(stage);
            applyEventFields(episode, event, item, defaultEventTypeCode, true);
            if (event.getRegisteredByUser() == null) event.setRegisteredByUser(currentUser);
            event = eventRepository.save(event);
            updateCitationStatusIfAttendance(event);
            applyTreatmentEntryIfNeeded(episode, stage, event);
            audit(episode, stage, event, "CORRECCION_ADMINISTRATIVA_CREAR_EVENTO", null, snapshotEvent(event), correctionReason, currentUser);
            inc(response, "createdEvents");
            incAudit(response);
            return;
        }

        if (eventId == null) throw badRequest("Para actualizar evento debe indicar id o eventId.");
        EpisodeEventEntity event = findEpisodeEvent(episode.getId(), eventId);
        String before = snapshotEvent(event);
        if (item.getStageId() != null) event.setStage(resolveStage(episode, item.getStageId()));
        applyEventFields(episode, event, item, defaultEventTypeCode, false);
        event = eventRepository.save(event);
        updateCitationStatusIfAttendance(event);
        applyTreatmentEntryIfNeeded(episode, event.getStage(), event);
        audit(episode, event.getStage() != null ? event.getStage() : defaultStage, event,
                "CORRECCION_ADMINISTRATIVA_ACTUALIZAR_EVENTO", before, snapshotEvent(event), correctionReason, currentUser);
        inc(response, "updatedEvents");
        incAudit(response);
    }

    private void applyEventFields(EpisodeEntity episode, EpisodeEventEntity event,
                                  AdministrativeEventCorrectionDTO item,
                                  String defaultEventTypeCode,
                                  boolean creating) {
        if (creating || item.getEventTypeId() != null || hasText(item.getEventTypeCode()) || hasText(defaultEventTypeCode)) {
            event.setEventType(resolveEventType(item.getEventTypeId(), firstText(item.getEventTypeCode(), defaultEventTypeCode)));
        }
        if (item.getRelatedEventId() != null) event.setRelatedEvent(resolveRelatedEvent(episode, item.getRelatedEventId()));
        if (hasText(item.getCitationTypeCode())) event.setCitationType(resolveCitationType(item.getCitationTypeCode()));
        if (hasText(item.getBiopsychosocialCommitmentCode())) event.setBiopsychosocialCommitmentLevel(resolveBiopsychosocialCommitmentLevel(item.getBiopsychosocialCommitmentCode()));
        if (item.getEventDate() != null) event.setEventDate(item.getEventDate());
        if (item.getEventTime() != null) event.setEventTime(item.getEventTime());
        if (item.getAttendanceStatusId() != null || hasText(item.getAttendanceStatusCode())) event.setAttendanceStatus(resolveAttendanceStatus(item.getAttendanceStatusId(), item.getAttendanceStatusCode()));
        if (item.getProfessionName() != null) event.setProfessionName(item.getProfessionName());
        if (item.getProfessionalUserId() != null) event.setProfessionalUser(user(item.getProfessionalUserId()));
        if (item.getProgramProfessionalId() != null) event.setProgramProfessional(programProfessional(item.getProgramProfessionalId()));
        if (item.getProgramId() != null) event.setProgram(program(item.getProgramId()));
        else if (creating && event.getStage() != null) event.setProgram(event.getStage().getProgram());
        if (item.getComment() != null) event.setComment(item.getComment());
        if (item.getCitationComment() != null) event.setCitationComment(item.getCitationComment());
        if (item.getObservation() != null) event.setObservation(item.getObservation());
        if (item.getNextAction() != null) event.setNextAction(item.getNextAction());
        if (item.getNextActionDate() != null) event.setNextActionDate(item.getNextActionDate());
        if (item.getResultCode() != null) event.setResultCode(item.getResultCode());
        if (item.getStateCode() != null) event.setStateCode(item.getStateCode());

        if (creating) {
            if (event.getEventDate() == null) event.setEventDate(LocalDate.now());
            if (event.getEventTime() == null) event.setEventTime(java.time.LocalTime.now());
            if (event.getEventType() == null) throw badRequest("Debe indicar eventTypeCode/eventTypeId para crear el evento.");
        }
    }

    private void updateCitationStatusIfAttendance(EpisodeEventEntity event) {
        if (event == null || event.getEventType() == null || !"ASISTENCIA".equalsIgnoreCase(event.getEventType().getCode())) return;
        if (event.getRelatedEvent() == null || event.getAttendanceStatus() == null) return;
        EpisodeEventEntity citation = event.getRelatedEvent();
        if (citation.getEventType() == null || !"CITACION".equalsIgnoreCase(citation.getEventType().getCode())) return;
        citation.setAttendanceStatus(event.getAttendanceStatus());
        eventRepository.save(citation);
    }

    private void applyTreatmentEntryIfNeeded(EpisodeEntity episode, EpisodeStageEntity stage, EpisodeEventEntity event) {
        if (event == null || event.getEventType() == null || !"RETROALIMENTACION".equalsIgnoreCase(event.getEventType().getCode())) return;
        if (!RESULT_TREATMENT_ENTRY.equalsIgnoreCase(Optional.ofNullable(event.getResultCode()).orElse(""))) return;
        LocalDateTime at = LocalDateTime.of(
                event.getEventDate() != null ? event.getEventDate() : LocalDate.now(),
                event.getEventTime() != null ? event.getEventTime() : java.time.LocalTime.now()
        );
        episode.setEntryToTreatmentAt(at);
        episode.setWaitingStopped(true);
        if (stage != null) {
            stage.setResultCode(RESULT_TREATMENT_ENTRY);
            stageRepository.save(stage);
        }
        episodeRepository.save(episode);
    }

    private void recomputeCitationAttendance(Integer episodeId, EpisodeEventEntity citation) {
        if (citation == null || citation.getId() == null) return;
        Optional<EpisodeEventEntity> latest = eventRepository.findByEpisodeIdOrderByEventDateAscEventTimeAscIdAsc(episodeId).stream()
                .filter(ev -> ev.getRelatedEvent() != null && Objects.equals(ev.getRelatedEvent().getId(), citation.getId()))
                .filter(ev -> ev.getEventType() != null && "ASISTENCIA".equalsIgnoreCase(ev.getEventType().getCode()))
                .filter(ev -> ev.getAttendanceStatus() != null)
                .max(Comparator
                        .comparing((EpisodeEventEntity ev) -> Optional.ofNullable(ev.getEventDate()).orElse(LocalDate.MIN))
                        .thenComparing(ev -> Optional.ofNullable(ev.getEventTime()).orElse(java.time.LocalTime.MIN))
                        .thenComparing(ev -> Optional.ofNullable(ev.getId()).orElse(0)));
        citation.setAttendanceStatus(latest.map(EpisodeEventEntity::getAttendanceStatus).orElseGet(() -> attendanceStatusRepository.findByCodeIgnoreCase("AGENDADO").orElse(null)));
        eventRepository.save(citation);
    }

    private EpisodeStageEntity resolveTargetStage(EpisodeEntity episode, Integer stageId, Integer programId) {
        if (stageId != null) return resolveStage(episode, stageId);
        if (programId != null) {
            return stageRepository.findFirstByEpisodeIdAndProgramIdOrderByStageOrderDescIdDesc(episode.getId(), programId)
                    .orElseThrow(() -> notFound("No existe una etapa del programa indicado dentro del episodio."));
        }
        if (episode.getCurrentStage() != null) return episode.getCurrentStage();
        return stageRepository.findFirstByEpisodeIdAndCurrentTrueOrderByStageOrderDesc(episode.getId()).orElse(null);
    }

    private EpisodeStageEntity resolveStage(EpisodeEntity episode, Integer stageId) {
        EpisodeStageEntity stage = stageRepository.findById(stageId).orElseThrow(() -> notFound("Etapa no encontrada"));
        ensureStageBelongsToEpisode(episode, stage);
        return stage;
    }

    private void ensureStageBelongsToEpisode(EpisodeEntity episode, EpisodeStageEntity stage) {
        if (stage == null || stage.getEpisode() == null || !Objects.equals(stage.getEpisode().getId(), episode.getId())) {
            throw badRequest("La etapa no pertenece al episodio indicado.");
        }
    }

    private EpisodeEventEntity findEpisodeEvent(Integer episodeId, Integer eventId) {
        EpisodeEventEntity event = eventRepository.findById(eventId).orElseThrow(() -> notFound("Evento no encontrado"));
        if (event.getEpisode() == null || !Objects.equals(event.getEpisode().getId(), episodeId)) {
            throw badRequest("El evento indicado no pertenece al episodio solicitado.");
        }
        return event;
    }

    private EpisodeEventEntity resolveRelatedEvent(EpisodeEntity episode, Integer relatedEventId) {
        if (relatedEventId == null) return null;
        return findEpisodeEvent(episode.getId(), relatedEventId);
    }

    private EpisodeSubstanceEntity findEpisodeSubstance(Integer episodeId, Integer id) {
        EpisodeSubstanceEntity entity = episodeSubstanceRepository.findById(id).orElseThrow(() -> notFound("Sustancia asociada al episodio no encontrada"));
        if (entity.getEpisode() == null || !Objects.equals(entity.getEpisode().getId(), episodeId)) {
            throw badRequest("La sustancia indicada no pertenece al episodio solicitado.");
        }
        return entity;
    }

    private void ensureSubstanceNotDuplicated(Integer episodeId, Integer substanceId, Integer currentAssociationId) {
        boolean exists = currentAssociationId == null
                ? episodeSubstanceRepository.findByEpisodeIdAndSubstanceId(episodeId, substanceId).isPresent()
                : episodeSubstanceRepository.findByEpisodeIdAndSubstanceIdAndIdNot(episodeId, substanceId, currentAssociationId).isPresent();
        if (exists) throw new ResponseStatusException(HttpStatus.CONFLICT, "La sustancia ya se encuentra asociada al episodio.");
    }

    private void demoteOtherPrimarySubstances(Integer episodeId, Integer exceptId) {
        for (EpisodeSubstanceEntity s : episodeSubstanceRepository.findByEpisodeIdAndPrimarySubstanceTrue(episodeId)) {
            if (exceptId == null || !Objects.equals(s.getId(), exceptId)) {
                s.setPrimarySubstance(false);
                episodeSubstanceRepository.save(s);
            }
        }
    }

    private void promoteFirstRemainingSubstanceAsPrimary(Integer episodeId) {
        List<EpisodeSubstanceEntity> remaining = episodeSubstanceRepository.findByEpisodeId(episodeId).stream()
                .sorted(Comparator
                        .comparing((EpisodeSubstanceEntity s) -> s.getUseOrder() != null ? s.getUseOrder() : Integer.MAX_VALUE)
                        .thenComparing(EpisodeSubstanceEntity::getId))
                .toList();
        if (!remaining.isEmpty() && remaining.stream().noneMatch(s -> Boolean.TRUE.equals(s.getPrimarySubstance()))) {
            EpisodeSubstanceEntity first = remaining.get(0);
            first.setPrimarySubstance(true);
            if (first.getUseOrder() == null) first.setUseOrder(1);
            episodeSubstanceRepository.save(first);
        }
    }

    private Integer nextUseOrder(Integer episodeId) {
        return episodeSubstanceRepository.findByEpisodeId(episodeId).stream()
                .map(EpisodeSubstanceEntity::getUseOrder)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private void validateUseOrder(Integer useOrder) {
        if (useOrder == null || useOrder < 1) throw badRequest("useOrder debe ser un entero mayor o igual a 1.");
    }

    private EpisodeTypeEntity resolveEpisodeType(Integer id, String code) {
        if (id != null) return episodeTypeRepository.findById(id).orElseThrow(() -> notFound("Tipo de episodio no encontrado"));
        if (!hasText(code)) return null;
        return episodeTypeRepository.findByCodeIgnoreCase(code).orElseThrow(() -> notFound("Tipo de episodio no encontrado: " + code));
    }

    private EventTypeEntity resolveEventType(Integer id, String code) {
        if (id != null) return eventTypeRepository.findById(id).orElseThrow(() -> notFound("Tipo de evento no encontrado"));
        if (!hasText(code)) throw badRequest("Debe indicar eventTypeCode o eventTypeId.");
        return eventTypeRepository.findByCodeIgnoreCase(code).orElseThrow(() -> notFound("Tipo de evento no encontrado: " + code));
    }

    private AttendanceStatusEntity resolveAttendanceStatus(Integer id, String code) {
        if (id != null) return attendanceStatusRepository.findById(id).orElseThrow(() -> notFound("Estado de asistencia no encontrado"));
        if (!hasText(code)) return null;
        return attendanceStatusRepository.findByCodeIgnoreCase(code).orElseThrow(() -> notFound("Estado de asistencia no encontrado: " + code));
    }

    private CitationTypeEntity resolveCitationType(String code) {
        if (!hasText(code)) return null;
        return citationTypeRepository.findByCodeIgnoreCase(code).orElseThrow(() -> notFound("Tipo de citación no encontrado: " + code));
    }

    private BiopsychosocialCommitmentLevelEntity resolveBiopsychosocialCommitmentLevel(String code) {
        if (!hasText(code)) return null;
        return biopsychosocialCommitmentLevelRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> notFound("Nivel de compromiso biopsicosocial no encontrado: " + code));
    }

    private ClosureReasonEntity resolveClosureReason(Integer id, String code) {
        if (id != null) return closureReasonRepository.findById(id).orElseThrow(() -> notFound("Motivo de cierre no encontrado"));
        if (!hasText(code)) return null;
        return closureReasonRepository.findByCodeIgnoreCase(code).orElseThrow(() -> notFound("Motivo de cierre no encontrado: " + code));
    }

    private ProgramEntity program(Integer id) {
        return programRepository.findById(id).orElseThrow(() -> notFound("Programa no encontrado: " + id));
    }

    private ProgramProfessionalEntity programProfessional(Long id) {
        return programProfessionalRepository.findById(id).orElseThrow(() -> notFound("Facultativo no encontrado: " + id));
    }

    private UserEntity user(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> notFound("Usuario no encontrado: " + id));
    }

    private UserEntity currentUser(String email) {
        if (!hasText(email)) return null;
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    private SubstanceEntity substance(Integer id) {
        return substanceRepository.findById(id).orElseThrow(() -> notFound("Sustancia no encontrada: " + id));
    }

    private String resolveAction(String action, Integer id) {
        if (!hasText(action)) return id == null ? ACTION_CREATE : ACTION_UPDATE;
        String normalized = action.trim().toUpperCase(Locale.ROOT);
        if ("CREAR".equals(normalized)) return ACTION_CREATE;
        if ("ACTUALIZAR".equals(normalized) || "MODIFICAR".equals(normalized)) return ACTION_UPDATE;
        if ("ELIMINAR".equals(normalized) || "ANULAR".equals(normalized)) return ACTION_DELETE;
        if (ACTION_CREATE.equals(normalized) || ACTION_UPDATE.equals(normalized) || ACTION_DELETE.equals(normalized)) return normalized;
        throw badRequest("action debe ser CREATE, UPDATE o DELETE.");
    }

    private LocalDateTime parseOptionalDateTime(String value) {
        if (!hasText(value)) return null;
        String trimmed = value.trim();
        try { return LocalDateTime.parse(trimmed); } catch (DateTimeParseException ignored) {}
        try { return LocalDate.parse(trimmed).atStartOfDay(); } catch (DateTimeParseException ex) {
            throw badRequest("closureDate debe tener formato YYYY-MM-DD o YYYY-MM-DDTHH:mm:ss");
        }
    }

    private void audit(EpisodeEntity episode, EpisodeStageEntity stage, EpisodeEventEntity event,
                       String actionType, String previousValue, String newValue, String reason, UserEntity user) {
        auditLogRepository.save(EpisodeAuditLogEntity.builder()
                .episode(episode)
                .stage(stage)
                .event(event)
                .actionType(actionType)
                .previousValue(truncate(previousValue, 1900))
                .newValue(truncate(newValue, 1900))
                .reason(truncate(reason, 1900))
                .performedByUser(user)
                .build());
    }

    private String snapshotEpisode(EpisodeEntity e) {
        if (e == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.getId());
        m.put("episodeTypeId", e.getEpisodeType() != null ? e.getEpisodeType().getId() : null);
        m.put("previousTreatmentNumber", e.getPreviousTreatmentNumber());
        m.put("originalRequestDate", e.getOriginalRequestDate());
        m.put("initialProgramId", e.getInitialProgram() != null ? e.getInitialProgram().getId() : null);
        m.put("currentProgramId", e.getCurrentProgram() != null ? e.getCurrentProgram().getId() : null);
        m.put("currentStageId", e.getCurrentStage() != null ? e.getCurrentStage().getId() : null);
        m.put("stateCode", e.getStateCode());
        m.put("resultCode", e.getResultCode());
        m.put("closedAt", e.getClosedAt());
        m.put("closureReasonId", e.getClosureReason() != null ? e.getClosureReason().getId() : null);
        m.put("active", e.getActive());
        return m.toString();
    }

    private String snapshotStage(EpisodeStageEntity s) {
        if (s == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("programId", s.getProgram() != null ? s.getProgram().getId() : null);
        m.put("stateCode", s.getStateCode());
        m.put("resultCode", s.getResultCode());
        m.put("receivedAt", s.getReceivedAt());
        m.put("closedAt", s.getClosedAt());
        m.put("closureReasonId", s.getClosureReason() != null ? s.getClosureReason().getId() : null);
        m.put("current", s.getCurrent());
        return m.toString();
    }

    private String snapshotSubstance(EpisodeSubstanceEntity s) {
        if (s == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("substanceId", s.getSubstance() != null ? s.getSubstance().getId() : null);
        m.put("level", s.getLevel());
        m.put("primarySubstance", s.getPrimarySubstance());
        m.put("useOrder", s.getUseOrder());
        m.put("observation", s.getObservation());
        return m.toString();
    }

    private String snapshotEvent(EpisodeEventEntity ev) {
        if (ev == null) return null;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", ev.getId());
        m.put("stageId", ev.getStage() != null ? ev.getStage().getId() : null);
        m.put("eventType", ev.getEventType() != null ? ev.getEventType().getCode() : null);
        m.put("relatedEventId", ev.getRelatedEvent() != null ? ev.getRelatedEvent().getId() : null);
        m.put("citationType", ev.getCitationType() != null ? ev.getCitationType().getCode() : null);
        m.put("biopsychosocialCommitment", ev.getBiopsychosocialCommitmentLevel() != null ? ev.getBiopsychosocialCommitmentLevel().getCode() : null);
        m.put("eventDate", ev.getEventDate());
        m.put("eventTime", ev.getEventTime());
        m.put("attendanceStatus", ev.getAttendanceStatus() != null ? ev.getAttendanceStatus().getCode() : null);
        m.put("programId", ev.getProgram() != null ? ev.getProgram().getId() : null);
        m.put("resultCode", ev.getResultCode());
        m.put("stateCode", ev.getStateCode());
        m.put("comment", ev.getComment());
        m.put("observation", ev.getObservation());
        return m.toString();
    }

    private void inc(AdministrativeCorrectionResponse response, String key) {
        response.getCounters().merge(key, 1, Integer::sum);
    }

    private void incAudit(AdministrativeCorrectionResponse response) {
        response.setAuditRecords(Optional.ofNullable(response.getAuditRecords()).orElse(0) + 1);
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private <T> T firstNonNull(T first, T second) {
        return first != null ? first : second;
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first : second;
    }

    private String firstText(String first, String second, String third) {
        return firstText(first, firstText(second, third));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String truncate(String value, int max) {
        if (value == null || value.length() <= max) return value;
        return value.substring(0, max);
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
