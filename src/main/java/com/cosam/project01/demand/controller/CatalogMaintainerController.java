package com.cosam.project01.demand.controller;

import com.cosam.project01.demand.dto.CatalogMaintainerDTO;
import com.cosam.project01.demand.dto.CatalogMaintainerRequest;
import com.cosam.project01.demand.service.CatalogMaintainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/demand/maintainers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO')")
public class CatalogMaintainerController {

    private final CatalogMaintainerService service;

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> supportedCatalogs() {
        return ResponseEntity.ok(service.supportedCatalogs());
    }

    @GetMapping("/{catalog}")
    public ResponseEntity<List<CatalogMaintainerDTO>> list(
            @PathVariable String catalog,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(service.list(catalog, q, active));
    }

    @GetMapping("/{catalog}/getAllPaginated")
    public ResponseEntity<Page<CatalogMaintainerDTO>> listPaginated(
            @PathVariable String catalog,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.listPaginated(catalog, q, active, pageable));
    }

    @GetMapping("/{catalog}/{id}")
    public ResponseEntity<CatalogMaintainerDTO> getById(
            @PathVariable String catalog,
            @PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(catalog, id));
    }

    @PostMapping("/{catalog}")
    public ResponseEntity<CatalogMaintainerDTO> create(
            @PathVariable String catalog,
            @RequestBody CatalogMaintainerRequest request) {
        return ResponseEntity.ok(service.create(catalog, request));
    }

    @PutMapping("/{catalog}/{id}")
    public ResponseEntity<CatalogMaintainerDTO> update(
            @PathVariable String catalog,
            @PathVariable Integer id,
            @RequestBody CatalogMaintainerRequest request) {
        return ResponseEntity.ok(service.update(catalog, id, request));
    }

    @DeleteMapping("/{catalog}/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String catalog,
            @PathVariable Integer id) {
        service.delete(catalog, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{catalog}/{id}/restore")
    public ResponseEntity<Void> restore(
            @PathVariable String catalog,
            @PathVariable Integer id) {
        service.restore(catalog, id);
        return ResponseEntity.noContent().build();
    }
}
