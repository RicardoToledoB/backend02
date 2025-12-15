package com.cosam.project01.controller;

import com.cosam.project01.dto.RegisterMovementDTO;
import com.cosam.project01.service.impl.CommuneServiceImpl;
import com.cosam.project01.service.impl.RegisterMovementServiceImpl;
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
@RequestMapping("/api/v1/registers_movements")
//@CrossOrigin("*")
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO')")
public class RegisterMovementController {

    @Autowired
    private RegisterMovementServiceImpl service;

    @PostMapping
    public ResponseEntity<RegisterMovementDTO> create(@RequestBody RegisterMovementDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RegisterMovementDTO>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }


    @GetMapping("/getAllPaginated")
    public ResponseEntity<Page<RegisterMovementDTO>> getAllPaginated(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaginated(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegisterMovementDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegisterMovementDTO> update(@PathVariable Integer id, @RequestBody RegisterMovementDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* SOFT DELETE */
    @GetMapping
    public ResponseEntity<List<RegisterMovementDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<RegisterMovementDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }



    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searchByRegisterId")
    public ResponseEntity<Page<RegisterMovementDTO>> searchByRegisterId(
            @RequestParam(required = false) Integer registerId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.searchByRegisterId(registerId, pageable));
    }

    @DeleteMapping("/by-register/{registerId}")
    public ResponseEntity<?> softDeleteByRegister(@PathVariable Integer registerId) {
        int affected = service.deleteByRegisterId(registerId);
        return ResponseEntity.ok("Movimientos marcados como eliminados: " + affected);
    }

}
