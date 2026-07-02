package com.cosam.project01.service.impl;

import com.cosam.project01.dto.ProfessionDTO;
import com.cosam.project01.entity.ProfessionEntity;
import com.cosam.project01.repository.ProfessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionServiceImpl {

    private final ProfessionRepository repository;

    @Transactional
    public ProfessionDTO create(ProfessionDTO dto) {
        if (dto == null || dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name es obligatorio");
        }
        ProfessionEntity entity = ProfessionEntity.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .active(dto.getActive() == null ? true : dto.getActive())
                .build();
        return toDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ProfessionDTO getById(Integer id) {
        return toDTO(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesión no encontrada")));
    }

    @Transactional(readOnly = true)
    public List<ProfessionDTO> listActive() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProfessionDTO> listAll() {
        return repository.findAllIncludingDeleted().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProfessionDTO> listDeleted() {
        return repository.findAllDeleted().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProfessionDTO> getAllPaginated(String q, Pageable pageable) {
        return repository.search(q, pageable).map(this::toDTO);
    }

    @Transactional
    public ProfessionDTO update(Integer id, ProfessionDTO dto) {
        ProfessionEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesión no encontrada"));
        if (dto.getCode() != null) entity.setCode(dto.getCode());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getActive() != null) entity.setActive(dto.getActive());
        return toDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        ProfessionEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesión no encontrada"));
        entity.setActive(false);
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }

    @Transactional
    public void restore(Integer id) {
        ProfessionEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesión no encontrada"));
        entity.setDeletedAt(null);
        entity.setActive(true);
        repository.save(entity);
    }

    private ProfessionDTO toDTO(ProfessionEntity entity) {
        return ProfessionDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}
