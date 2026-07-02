package com.cosam.project01.controller;

import com.cosam.project01.dto.ProfessionDTO;
import com.cosam.project01.service.impl.ProfessionServiceImpl;
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
@RequestMapping("/api/v1/professions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATIVO','ROLE_SUPERVISOR')")
public class ProfessionController {

    private final ProfessionServiceImpl service;

    @GetMapping
    public ResponseEntity<List<ProfessionDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProfessionDTO>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<ProfessionDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }

    @GetMapping("/getAllPaginated")
    public ResponseEntity<Page<ProfessionDTO>> getAllPaginated(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaginated(q, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProfessionDTO> create(@RequestBody ProfessionDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessionDTO> update(@PathVariable Integer id, @RequestBody ProfessionDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }
}
