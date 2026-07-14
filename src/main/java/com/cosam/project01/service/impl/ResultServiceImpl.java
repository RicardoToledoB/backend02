package com.cosam.project01.service.impl;

import com.cosam.project01.dto.ResultDTO;
import com.cosam.project01.entity.ResultEntity;
import com.cosam.project01.repository.CommuneRepository;
import com.cosam.project01.repository.ResultRepository;
import com.cosam.project01.service.ICommuneService;
import com.cosam.project01.service.IResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultServiceImpl implements IResultService {

    @Autowired
    private ResultRepository repository;


    private ResultDTO mapToDTO(ResultEntity entity) {
        return ResultDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .scope(entity.getScope())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private ResultEntity mapToEntity(ResultDTO dto) {
        return ResultEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .scope(dto.getScope())
                .description(dto.getDescription())
                .active(dto.getActive())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    public ResultDTO create(ResultDTO dto) {
        ResultEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public ResultDTO update(Integer id, ResultDTO dto) {
        // Usar findAnyById permite actualizar también registros históricos/base
        // aunque en el futuro estuvieran eliminados lógicamente. No existe una
        // regla de negocio que bloquee la edición de los IDs 1-8.
        ResultEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resultado no encontrado"));

        if (dto.getCode() != null && !dto.getCode().isBlank()) {
            String normalizedCode = dto.getCode().trim().toUpperCase();

            repository.findActiveDuplicateCode(id, normalizedCode).ifPresent(existing -> {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Ya existe un resultado activo con el código " + normalizedCode
                );
            });

            // Si el código estaba ocupado por duplicados eliminados lógicamente
            // 9-16 u otros, se libera para que el registro histórico/base pueda
            // recibir el código solicitado sin violar UNIQUE(code).
            repository.neutralizeDeletedDuplicatesByCode(id, normalizedCode);
            entity.setCode(normalizedCode);
        }

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getScope() != null) entity.setScope(dto.getScope());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getActive() != null) entity.setActive(dto.getActive());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public ResultDTO getById(Integer id) {
        ResultEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<ResultDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.softDeleteAndDeactivate(id);
    }
    public Page<ResultDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<ResultDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<ResultDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<ResultDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<ResultDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        int updated = repository.restoreAndActivate(id);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resultado no encontrado");
        }
    }
}
