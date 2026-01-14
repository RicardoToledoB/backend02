package com.cosam.project01.controller;

import com.cosam.project01.dto.PostulantDTO;
import com.cosam.project01.service.impl.PostulantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/postulants")
public class PostulantController {

    @Autowired
    private PostulantServiceImpl service;

    // ========================
    // CREATE
    // ========================
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
    @PostMapping
    public ResponseEntity<PostulantDTO> create(@RequestBody PostulantDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // ========================
    // READ
    // ========================
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PostulantDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
    @GetMapping
    public ResponseEntity<List<PostulantDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
    @GetMapping("/all")
    public ResponseEntity<List<PostulantDTO>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
    @GetMapping("/getAllPaginated")
    public ResponseEntity<Page<PostulantDTO>> getAllPaginated(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(service.getAllPaginated(name, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
    @GetMapping("/searchByRut")
    public ResponseEntity<Page<PostulantDTO>> searchByRut(
            @RequestParam(required = false) String rut,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(service.searchByRut(rut, pageable));
    }

    // ========================
    // UPDATE
    // ========================
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
    @PutMapping("/{id}")
    public ResponseEntity<PostulantDTO> update(
            @PathVariable Integer id,
            @RequestBody PostulantDTO dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    // ========================
    // SOFT DELETE (ADMIN)
    // ========================
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
    @GetMapping("/deleted")
    public ResponseEntity<List<PostulantDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }
}
