package com.cosam.project01.controller;
import com.cosam.project01.dto.UserProgramDTO;
import com.cosam.project01.service.impl.UserProgramServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users_programs")
//@CrossOrigin("*")
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO')")
public class UserProgramController {

    @Autowired
    private UserProgramServiceImpl service;

    @PostMapping
    public ResponseEntity<UserProgramDTO> create(@RequestBody UserProgramDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    

    @GetMapping("/{id}")
    public ResponseEntity<UserProgramDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProgramDTO> update(@PathVariable Integer id, @RequestBody UserProgramDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/all")
    public ResponseEntity<List<UserProgramDTO>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }
    



    /* SOFT DELETE */
    @GetMapping
    public ResponseEntity<List<UserProgramDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<UserProgramDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }


    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }


}
