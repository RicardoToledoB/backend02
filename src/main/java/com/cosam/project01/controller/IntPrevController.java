package com.cosam.project01.controller;

import com.cosam.project01.dto.IntPrevDTO;
import com.cosam.project01.service.impl.CommuneServiceImpl;
import com.cosam.project01.service.impl.IntPrevServiceImpl;
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
@RequestMapping("/api/v1/int_prevs")
//@CrossOrigin("*")
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')")
public class IntPrevController {

    @Autowired
    private IntPrevServiceImpl service;

    @PostMapping
    public ResponseEntity<IntPrevDTO> create(@RequestBody IntPrevDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<IntPrevDTO>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }


    @GetMapping("/getAllPaginated")
    public ResponseEntity<Page<IntPrevDTO>> getAllPaginated(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaginated(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntPrevDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IntPrevDTO> update(@PathVariable Integer id, @RequestBody IntPrevDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* SOFT DELETE */
    @GetMapping
    public ResponseEntity<List<IntPrevDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<IntPrevDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }



    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }
}
