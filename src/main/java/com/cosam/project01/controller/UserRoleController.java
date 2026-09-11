package com.cosam.project01.controller;

import com.cosam.project01.dto.UserRoleDTO;
import com.cosam.project01.service.impl.UserRoleServiceImpl;
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
@RequestMapping("/api/v1/users_roles")
//@CrossOrigin("*")
public class UserRoleController {

    private static final String READ_ROLES = "hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR')";
    private static final String WRITE_ROLES = "hasAnyRole('ADMIN','ADMINISTRATIVO')";

    @Autowired
    private UserRoleServiceImpl service;

    @PostMapping
    @PreAuthorize(WRITE_ROLES)
    public ResponseEntity<UserRoleDTO> create(@RequestBody UserRoleDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize(READ_ROLES)
    public ResponseEntity<UserRoleDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize(WRITE_ROLES)
    public ResponseEntity<UserRoleDTO> update(@PathVariable Integer id, @RequestBody UserRoleDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(WRITE_ROLES)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize(READ_ROLES)
    public ResponseEntity<List<UserRoleDTO>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/getAllPaginated")
    @PreAuthorize(READ_ROLES)
    public ResponseEntity<Page<UserRoleDTO>> getAllPaginated(
            @RequestParam(required = false) Integer id,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaginated(id, pageable));
    }

    /* SOFT DELETE */
    @GetMapping
    @PreAuthorize(READ_ROLES)
    public ResponseEntity<List<UserRoleDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/deleted")
    @PreAuthorize(READ_ROLES)
    public ResponseEntity<List<UserRoleDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize(WRITE_ROLES)
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    @PreAuthorize(READ_ROLES)
    public ResponseEntity<List<UserRoleDTO>> findByUserId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getRolesByUser(id));
    }

    @DeleteMapping("/user/{userId}")
    @PreAuthorize(WRITE_ROLES)
    public ResponseEntity<Void> deleteByUserId(@PathVariable Integer userId) {
        service.deleteByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/role/{roleId}")
    @PreAuthorize(WRITE_ROLES)
    public ResponseEntity<Void> deleteByUserAndRole(@PathVariable Integer userId, @PathVariable Integer roleId) {
        service.deleteByUserAndRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}
