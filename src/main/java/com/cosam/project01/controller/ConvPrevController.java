package com.cosam.project01.controller;

import com.cosam.project01.dto.ConvPrevDTO;
import com.cosam.project01.service.impl.ConvPrevServiceImpl;
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
@RequestMapping("/api/v1/conv_prevs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATIVO','ROLE_SUPERVISOR')")
public class ConvPrevController {

    private final ConvPrevServiceImpl service;

    @GetMapping
    public ResponseEntity<List<ConvPrevDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ConvPrevDTO>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<ConvPrevDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }

    @GetMapping("/getAllPaginated")
    public ResponseEntity<Page<ConvPrevDTO>> getAllPaginated(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer intPrevId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaginated(q, intPrevId, pageable));
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<ConvPrevDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findByIdIncludingDeleted(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConvPrevDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<ConvPrevDTO> create(@RequestBody ConvPrevDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConvPrevDTO> update(@PathVariable Integer id, @RequestBody ConvPrevDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/softDelete/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/restore/{id}")
    public ResponseEntity<Void> restorePatch(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<Void> restorePut(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restorePost(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }
}
