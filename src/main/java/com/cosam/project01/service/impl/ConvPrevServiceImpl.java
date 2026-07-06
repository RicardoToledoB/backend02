package com.cosam.project01.service.impl;

import com.cosam.project01.dto.ConvPrevDTO;
import com.cosam.project01.dto.IntPrevDTO;
import com.cosam.project01.entity.ConvPrevEntity;
import com.cosam.project01.entity.IntPrevEntity;
import com.cosam.project01.repository.ConvPrevRepository;
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
public class ConvPrevServiceImpl {

    private final ConvPrevRepository repository;
    private final IntPrevRepository intPrevRepository;

    @Transactional
    public ConvPrevDTO create(ConvPrevDTO dto) {
        if (dto == null || dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name es obligatorio");
        }

        ConvPrevEntity entity = ConvPrevEntity.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .active(dto.getActive() == null ? true : dto.getActive())
                .intPrev(resolveIntPrev(resolveIntPrevId(dto)))
                .build();

        return toDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ConvPrevDTO getById(Integer id) {
        return toDTO(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convenio previsional no encontrado")));
    }

    @Transactional(readOnly = true)
    public ConvPrevDTO findByIdIncludingDeleted(Integer id) {
        return toDTO(repository.findAnyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convenio previsional no encontrado")));
    }

    @Transactional(readOnly = true)
    public List<ConvPrevDTO> listActive() {
        return repository.findAllActive().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConvPrevDTO> listAll() {
        return repository.findAllIncludingDeleted().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConvPrevDTO> listDeleted() {
        return repository.findAllDeleted().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ConvPrevDTO> getAllPaginated(String q, Integer intPrevId, Pageable pageable) {
        return repository.search(q, intPrevId, pageable).map(this::toDTO);
    }

    @Transactional
    public ConvPrevDTO update(Integer id, ConvPrevDTO dto) {
        ConvPrevEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convenio previsional no encontrado"));

        if (dto.getCode() != null) entity.setCode(dto.getCode());
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getActive() != null) entity.setActive(dto.getActive());
        Integer requestedIntPrevId = resolveIntPrevId(dto);
        if (requestedIntPrevId != null) entity.setIntPrev(resolveIntPrev(requestedIntPrevId));

        return toDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        ConvPrevEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convenio previsional no encontrado"));
        entity.setActive(false);
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }

    @Transactional
    public void restore(Integer id) {
        ConvPrevEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convenio previsional no encontrado"));
        entity.setDeletedAt(null);
        entity.setActive(true);
        repository.save(entity);
    }


    private Integer resolveIntPrevId(ConvPrevDTO dto) {
        if (dto == null) return null;
        if (dto.getIntPrevId() != null) return dto.getIntPrevId();
        if (dto.getIntPrev() != null) return dto.getIntPrev().getId();
        return null;
    }

    private IntPrevEntity resolveIntPrev(Integer id) {
        if (id == null) return null;
        return intPrevRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de previsión padre no encontrado"));
    }

    private ConvPrevDTO toDTO(ConvPrevEntity entity) {
        IntPrevEntity intPrev = entity.getIntPrev();
        return ConvPrevDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.getActive())
                .intPrevId(intPrev != null ? intPrev.getId() : null)
                .intPrevCode(intPrev != null ? intPrev.getCode() : null)
                .intPrevName(intPrev != null ? intPrev.getName() : null)
                .intPrev(intPrev != null ? IntPrevDTO.builder()
                        .id(intPrev.getId())
                        .code(intPrev.getCode())
                        .name(intPrev.getName())
                        .description(intPrev.getDescription())
                        .active(intPrev.getActive())
                        .createdAt(intPrev.getCreatedAt())
                        .updatedAt(intPrev.getUpdatedAt())
                        .deletedAt(intPrev.getDeletedAt())
                        .build() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}
