package com.cosam.project01.controller;
import com.cosam.project01.dto.UserRoleDTO;
import com.cosam.project01.service.impl.UserRoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users_roles")
@CrossOrigin("*")
public class UserRoleController {

    @Autowired
    private UserRoleServiceImpl service;

    @PostMapping
    public ResponseEntity<UserRoleDTO> create(@RequestBody UserRoleDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    

    @GetMapping("/{id}")
    public ResponseEntity<UserRoleDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRoleDTO> update(@PathVariable Integer id, @RequestBody UserRoleDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/all")
    public ResponseEntity<List<UserRoleDTO>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }
    
    @GetMapping("/getAllPaginated")
    public ResponseEntity<Page<UserRoleDTO>> getAllPaginated(
            @RequestParam(required = false) Integer id,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaginated(id, pageable));
    }


    /* SOFT DELETE */
    @GetMapping
    public ResponseEntity<List<UserRoleDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<UserRoleDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }



    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }
}
