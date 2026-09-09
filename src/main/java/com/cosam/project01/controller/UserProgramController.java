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
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATIVO','ROLE_SUPERVISOR','ROLE_PROFESIONAL','ROLE_EJECUTIVO')")
public class UserProgramController {

    @Autowired
    private UserProgramServiceImpl service;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserProgramDTO> create(@RequestBody UserProgramDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteByUserId(@PathVariable Integer userId) {
        service.deleteByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/communications")
    public ResponseEntity<List<UserProgramDTO>> getCommunicationConfigurations(
            @RequestParam(required = false) Integer programId,
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(service.getCommunicationConfigurations(programId, type));
    }

    @GetMapping("/communications/recipients")
    public ResponseEntity<List<UserProgramDTO>> getCommunicationRecipients(
            @RequestParam Integer programId,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(service.getCommunicationRecipients(programId, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProgramDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserProgramDTO> update(@PathVariable Integer id, @RequestBody UserProgramDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<UserProgramDTO>> findByUserId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getUserProgramByUser(id));
    }

    @DeleteMapping("/user/{userId}/program/{programId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteByUserAndProgram(@PathVariable Integer userId, @PathVariable Integer programId) {
        service.deleteByUserAndProgram(userId, programId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/transversal")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTransversalByUser(@PathVariable Integer userId) {
        service.deleteTransversalByUser(userId);
        return ResponseEntity.noContent().build();
    }


}
