package com.cosam.project01.service.impl;

import com.cosam.project01.dto.IntPrevDTO;
import com.cosam.project01.entity.IntPrevEntity;
import com.cosam.project01.repository.IntPrevRepository;
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
public class IntPrevServiceImpl {

    private final IntPrevRepository repository;

    @Transactional
    public IntPrevDTO create(IntPrevDTO dto) {
        if (dto == null || dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name es obligatorio");
        }
        IntPrevEntity entity = IntPrevEntity.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .active(dto.getActive() == null ? true : dto.getActive())
                .build();
        return toDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public IntPrevDTO getById(Integer id) {
        return toDTO(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de previsión no encontrado")));
    }

    @Transactional(readOnly = true)
    public IntPrevDTO findByIdIncludingDeleted(Integer id) {
        return toDTO(repository.findAnyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de previsión no encontrado")));
    }

    @Transactional(readOnly = true)
    public List<IntPrevDTO> listActive() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IntPrevDTO> listAll() {
        return repository.findAllIncludingDeleted().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IntPrevDTO> listDeleted() {
        return repository.findAllDeleted().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<IntPrevDTO> getAllPaginated(String q, Pageable pageable) {
        return repository.search(q, pageable).map(this::toDTO);
    }

    @Transactional
    public IntPrevDTO update(Integer id, IntPrevDTO dto) {
        IntPrevEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de previsión no encontrado"));
        if (dto.getCode() != null) entity.setCode(dto.getCode());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getActive() != null) entity.setActive(dto.getActive());
        return toDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        IntPrevEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de previsión no encontrado"));
        entity.setActive(false);
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }

    @Transactional
    public void restore(Integer id) {
        IntPrevEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de previsión no encontrado"));
        entity.setDeletedAt(null);
        entity.setActive(true);
        repository.save(entity);
    }

    private IntPrevDTO toDTO(IntPrevEntity entity) {
        return IntPrevDTO.builder()
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
