package com.cosam.project01.demand.controller;

import com.cosam.project01.demand.dto.*;
import com.cosam.project01.demand.service.DemandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/demand")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR','PROFESIONAL')")
public class DemandController {

    private final DemandService service;


    @GetMapping("/persons/rut/{rut}")
    public ResponseEntity<PostulantSummaryDTO> getPersonByRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.getPersonByRut(rut));
    }

    @GetMapping("/catalogs")
    public ResponseEntity<DemandCatalogsDTO> catalogs() {
        return ResponseEntity.ok(service.getCatalogs());
    }

    @PostMapping("/episodes")
    public ResponseEntity<EpisodeDTO> createEpisode(@Valid @RequestBody CreateEpisodeRequest request) {
        return ResponseEntity.ok(service.createEpisode(request));
    }

    @GetMapping("/episodes/{id}")
    public ResponseEntity<EpisodeDTO> getEpisode(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getEpisode(id));
    }

    @GetMapping("/episodes/{id}/longitudinal")
    public ResponseEntity<EpisodeLongitudinalDTO> getLongitudinalByEpisode(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getLongitudinalByEpisodeId(id));
    }

    @GetMapping("/episodes/active/by-rut/{rut}")
    public ResponseEntity<EpisodeDTO> getActiveByRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.getActiveByRut(rut));
    }

    @GetMapping("/episodes/by-rut/{rut}/longitudinal")
    public ResponseEntity<EpisodeLongitudinalDTO> getLongitudinalByRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.getLongitudinalByRut(rut));
    }

    @GetMapping("/episodes/prioritized")
    public ResponseEntity<Page<PrioritizedEpisodeDTO>> prioritized(
            @RequestParam(required = false) Integer programId,
            @RequestParam(required = false) String stateCode,
            @RequestParam(required = false) String resultCode,
            @PageableDefault(size = 20, sort = "originalRequestDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.getPrioritized(programId, stateCode, resultCode, pageable));
    }

    @GetMapping("/dashboard/supervisor")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    public ResponseEntity<DemandDashboardDTO> dashboard() {
        return ResponseEntity.ok(service.getDashboard());
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
    public ResponseEntity<EpisodeReferenceDTO> referenceEpisode(@PathVariable Integer id, @Valid @RequestBody ReferenceEpisodeRequest request) {
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
    public ResponseEntity<EpisodeDTO> close(@PathVariable Integer id, @Valid @RequestBody CloseEpisodeRequest request) {
        return ResponseEntity.ok(service.closeEpisode(id, request));
    }

    @PostMapping("/episodes/{id}/reverse")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    public ResponseEntity<EpisodeDTO> reverse(@PathVariable Integer id, @Valid @RequestBody ReverseEpisodeRequest request) {
        return ResponseEntity.ok(service.reverseEpisode(id, request));
    }

    @PostMapping("/episodes/{id}/alerts")
    public ResponseEntity<EpisodeAlertDTO> createAlert(@PathVariable Integer id, @RequestBody CreateAlertRequest request) {
        return ResponseEntity.ok(service.createAlert(id, request));
    }

    @PostMapping("/episodes/{id}/documents")
    public ResponseEntity<EpisodeDocumentDTO> createDocument(@PathVariable Integer id, @RequestBody CreateDocumentRequest request) {
        return ResponseEntity.ok(service.createDocument(id, request));
    }

    @PostMapping("/episodes/{id}/substances")
    public ResponseEntity<EpisodeSubstanceDTO> addSubstance(@PathVariable Integer id, @Valid @RequestBody CreateSubstanceRequest request) {
        return ResponseEntity.ok(service.addSubstance(id, request));
    }
}
