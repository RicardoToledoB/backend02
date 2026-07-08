package com.cosam.project01.controller;

import com.cosam.project01.dto.ProgramProfessionalDTO;
import com.cosam.project01.service.impl.ProgramProfessionalServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/program_professionals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATIVO','ROLE_SUPERVISOR')")
public class ProgramProfessionalController {

    private final ProgramProfessionalServiceImpl service;

    @GetMapping
    public ResponseEntity<List<ProgramProfessionalDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProgramProfessionalDTO>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<ProgramProfessionalDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }

    @GetMapping("/getAllPaginated")
    public ResponseEntity<Page<ProgramProfessionalDTO>> getAllPaginated(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer professionId,
            @RequestParam(required = false) Integer programId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaginated(q, professionId, programId, pageable));
    }

    @GetMapping("/program/{programId}")
    public ResponseEntity<List<ProgramProfessionalDTO>> listByProgram(@PathVariable Integer programId) {
        return ResponseEntity.ok(service.listByProgram(programId));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<ProgramProfessionalDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findByIdIncludingDeleted(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgramProfessionalDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProgramProfessionalDTO> create(@RequestBody ProgramProfessionalDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgramProfessionalDTO> update(@PathVariable Long id, @RequestBody ProgramProfessionalDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/softDelete/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restorePost(@PathVariable Long id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/restore/{id}")
    public ResponseEntity<Void> restorePatch(@PathVariable Long id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restorePut(@PathVariable Long id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }
}
