package com.cosam.project01.demand.controller;

import com.cosam.project01.demand.dto.*;
import com.cosam.project01.demand.service.DemandService;
import com.cosam.project01.demand.service.EpisodePurgeService;
import com.cosam.project01.security.RequestTokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/demand")
@RequiredArgsConstructor
public class DemandController {

    private final DemandService service;
    private final EpisodePurgeService episodePurgeService;
    private final RequestTokenValidator requestTokenValidator;

    @GetMapping("/persons/rut/{rut}")
    public ResponseEntity<PostulantSummaryDTO> getPersonByRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.getPersonByRut(rut));
    }

    @GetMapping("/catalogs")
    public ResponseEntity<DemandCatalogsDTO> catalogs() {
        return ResponseEntity.ok(service.getCatalogs());
    }

    @GetMapping("/document-types")
    public ResponseEntity<List<OptionDTO>> documentTypes() {
        return ResponseEntity.ok(service.getCatalogs().getDocumentTypes());
    }

    @PostMapping("/episodes")
    public ResponseEntity<EpisodeDTO> createEpisode(@Valid @RequestBody CreateEpisodeRequest request) {
        return ResponseEntity.ok(service.createEpisode(request));
    }


    @GetMapping("/episodes/catalogs")
    public ResponseEntity<DemandCatalogsDTO> episodeCatalogs() {
        return ResponseEntity.ok(service.getCatalogs());
    }

    @GetMapping("/episodes")
    public ResponseEntity<Page<PrioritizedEpisodeDTO>> listEpisodes(
            @RequestParam(required = false) Integer programId,
            @RequestParam(required = false) String stateCode,
            @RequestParam(required = false) String resultCode,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "originalRequestDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.getPrioritized(programId, stateCode, resultCode, search, pageable));
    }

    @GetMapping("/episodes/{id}")
    public ResponseEntity<EpisodeDTO> getEpisode(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getEpisode(id));
    }

    @GetMapping("/episodes/{id}/longitudinal")
    public ResponseEntity<EpisodeLongitudinalDTO> getLongitudinalByEpisode(@PathVariable Integer id, HttpServletRequest request) {
        requestTokenValidator.requireValidAccessToken(request);
        return ResponseEntity.ok(service.getLongitudinalByEpisodeId(id));
    }

    @GetMapping("/episodes/active/by-rut/{rut}")
    public ResponseEntity<EpisodeDTO> getActiveByRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.getActiveByRut(rut));
    }

    @GetMapping("/episodes/by-rut/{rut}/longitudinal")
    public ResponseEntity<EpisodeLongitudinalDTO> getLongitudinalByRut(@PathVariable String rut, HttpServletRequest request) {
        requestTokenValidator.requireValidAccessToken(request);
        return ResponseEntity.ok(service.getLongitudinalByRut(rut));
    }

    @GetMapping("/episodes/prioritized")
    public ResponseEntity<Page<PrioritizedEpisodeDTO>> prioritized(
            @RequestParam(required = false) Integer programId,
            @RequestParam(required = false) String stateCode,
            @RequestParam(required = false) String resultCode,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "originalRequestDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.getPrioritized(programId, stateCode, resultCode, search, pageable));
    }

    @PostMapping("/episodes/program-contexts")
    public ResponseEntity<List<EpisodeProgramContextDTO>> programContexts(@Valid @RequestBody EpisodeProgramContextRequest request) {
        return ResponseEntity.ok(service.getEpisodeProgramContexts(request));
    }

    @GetMapping("/dashboard/supervisor")
    public ResponseEntity<DemandDashboardDTO> dashboard() {
        return ResponseEntity.ok(service.getDashboard());
    }

    @GetMapping("/dashboard/supervisor/programs")
    public ResponseEntity<List<DemandSupervisorProgramDTO>> dashboardSupervisorPrograms() {
        return ResponseEntity.ok(service.getDashboardSupervisorPrograms());
    }

    @GetMapping("/dashboard/supervisor/programs/references")
    public ResponseEntity<List<DemandSupervisorProgramReferenceDTO>> dashboardSupervisorProgramReferences(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.getDashboardSupervisorProgramReferences(from, to));
    }

    @PostMapping("/episodes/{id}/events")
    public ResponseEntity<EpisodeEventDTO> createEvent(@PathVariable Integer id, @Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.ok(service.createEvent(id, request));
    }

    @PostMapping("/episodes/{id}/citations")
    public ResponseEntity<EpisodeEventDTO> createCitation(@PathVariable Integer id, @Valid @RequestBody CreateCitationRequest request) {
        return ResponseEntity.ok(service.createCitation(id, request));
    }

    @PostMapping("/episodes/{id}/attendance")
    public ResponseEntity<EpisodeEventDTO> registerAttendance(@PathVariable Integer id, @Valid @RequestBody RegisterAttendanceRequest request) {
        return ResponseEntity.ok(service.registerAttendance(id, request));
    }

    @PostMapping("/episodes/{id}/references")
    public ResponseEntity<EpisodeReferenceDTO> referenceEpisode(@PathVariable Integer id, @Valid @RequestBody ReferenceEpisodeRequest request, HttpServletRequest httpRequest) {
        requestTokenValidator.requireValidAccessToken(httpRequest);
        return ResponseEntity.ok(service.referenceEpisode(id, request));
    }

    @PostMapping("/episodes/{id}/treatment-entry")
    public ResponseEntity<EpisodeDTO> treatmentEntry(@PathVariable Integer id, @RequestBody TreatmentEntryRequest request) {
        return ResponseEntity.ok(service.registerTreatmentEntry(id, request));
    }

    @PostMapping("/episodes/{id}/egress")
    public ResponseEntity<EpisodeDTO> egress(@PathVariable Integer id, @RequestBody EgressEpisodeRequest request) {
        return ResponseEntity.ok(service.egressEpisode(id, request));
    }

    @PostMapping("/episodes/{id}/close")
    public ResponseEntity<EpisodeDTO> close(@PathVariable Integer id, @Valid @RequestBody CloseEpisodeRequest request, HttpServletRequest httpRequest) {
        requestTokenValidator.requireValidAccessToken(httpRequest);
        return ResponseEntity.ok(service.closeEpisode(id, request));
    }

    @PostMapping("/episodes/{id}/reverse")
    public ResponseEntity<EpisodeDTO> reverse(@PathVariable Integer id, @Valid @RequestBody ReverseEpisodeRequest request) {
        return ResponseEntity.ok(service.reverseEpisode(id, request));
    }

    @PostMapping("/episodes/{id}/alerts")
    public ResponseEntity<EpisodeAlertDTO> createAlert(@PathVariable Integer id, @RequestBody CreateAlertRequest request) {
        return ResponseEntity.ok(service.createAlert(id, request));
    }

    @GetMapping("/episodes/{episodeId}/documents")
    public ResponseEntity<List<EpisodeDocumentDTO>> listDocuments(@PathVariable Integer episodeId) {
        return ResponseEntity.ok(service.listDocuments(episodeId));
    }

    @PostMapping(value = "/episodes/{episodeId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EpisodeDocumentDTO> uploadDocument(
            @PathVariable Integer episodeId,
            @RequestPart("file") MultipartFile file,
            @RequestParam String documentTypeCode,
            @RequestParam(required = false) Integer stageId,
            @RequestParam(required = false) Integer eventId,
            @RequestParam(required = false) Integer referenceId) {
        return ResponseEntity.ok(service.uploadDocument(episodeId, file, documentTypeCode, stageId, eventId, referenceId));
    }

    @PostMapping(value = "/episodes/{episodeId}/documents/metadata", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EpisodeDocumentDTO> createDocumentMetadata(@PathVariable Integer episodeId, @RequestBody CreateDocumentRequest request) {
        return ResponseEntity.ok(service.createDocument(episodeId, request));
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<?> downloadDocument(
            @PathVariable Integer documentId,
            @RequestParam(defaultValue = "inline") String disposition) {
        DocumentDownloadDTO file = service.downloadDocument(documentId);
        boolean attachment = "attachment".equalsIgnoreCase(disposition);
        ContentDisposition contentDisposition = (attachment ? ContentDisposition.attachment() : ContentDisposition.inline())
                .filename(file.getFilename(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .body(file.getResource());
    }

    @PutMapping("/documents/{documentId}")
    public ResponseEntity<EpisodeDocumentDTO> updateDocument(@PathVariable Integer documentId, @RequestBody UpdateDocumentRequest request) {
        return ResponseEntity.ok(service.updateDocument(documentId, request));
    }

    @DeleteMapping("/episodes/{episodeId}/purge")
    public ResponseEntity<EpisodePurgeResponse> purgeEpisode(@PathVariable Integer episodeId, HttpServletRequest request) {
        requestTokenValidator.requireAdminAccessToken(request);
        return ResponseEntity.ok(episodePurgeService.purgeEpisode(episodeId));
    }

    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Integer documentId) {
        service.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/documents/{documentId}/replace", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EpisodeDocumentDTO> replaceDocument(
            @PathVariable Integer documentId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String documentTypeCode,
            @RequestParam(required = false) Integer stageId,
            @RequestParam(required = false) Integer eventId,
            @RequestParam(required = false) Integer referenceId) {
        return ResponseEntity.ok(service.replaceDocument(documentId, file, documentTypeCode, stageId, eventId, referenceId));
    }

    @PostMapping("/episodes/{episodeId}/send-email")
    public ResponseEntity<EmailNotificationResponse> sendEpisodeEmail(@PathVariable Integer episodeId, @Valid @RequestBody EmailNotificationRequest request) {
        request.setEpisodeId(episodeId);
        return ResponseEntity.ok(service.sendEmailNotification(request));
    }

    @PostMapping("/documents/{documentId}/send-email")
    public ResponseEntity<EmailNotificationResponse> sendDocumentEmail(@PathVariable Integer documentId, @Valid @RequestBody EmailNotificationRequest request) {
        request.setDocumentId(documentId);
        return ResponseEntity.ok(service.sendEmailNotification(request));
    }

    @PostMapping("/notifications/email")
    public ResponseEntity<EmailNotificationResponse> sendGeneralEmail(@Valid @RequestBody EmailNotificationRequest request) {
        return ResponseEntity.ok(service.sendEmailNotification(request));
    }

    @GetMapping("/episodes/{id}/substances")
    public ResponseEntity<List<EpisodeSubstanceDTO>> listSubstances(@PathVariable Integer id) {
        return ResponseEntity.ok(service.listSubstances(id));
    }

    @PostMapping("/episodes/{id}/substances")
    public ResponseEntity<EpisodeSubstanceDTO> addSubstance(@PathVariable Integer id, @Valid @RequestBody CreateSubstanceRequest request) {
        return ResponseEntity.ok(service.addSubstance(id, request));
    }

    @PutMapping("/episodes/{episodeId}/substances/{substanceAssociationId}")
    public ResponseEntity<EpisodeSubstanceDTO> updateSubstance(
            @PathVariable Integer episodeId,
            @PathVariable Integer substanceAssociationId,
            @RequestBody UpdateSubstanceRequest request) {
        return ResponseEntity.ok(service.updateSubstance(episodeId, substanceAssociationId, request));
    }

    @DeleteMapping("/episodes/{episodeId}/substances/{substanceAssociationId}")
    public ResponseEntity<Void> deleteSubstance(
            @PathVariable Integer episodeId,
            @PathVariable Integer substanceAssociationId) {
        service.deleteSubstance(episodeId, substanceAssociationId);
        return ResponseEntity.noContent().build();
    }
}
