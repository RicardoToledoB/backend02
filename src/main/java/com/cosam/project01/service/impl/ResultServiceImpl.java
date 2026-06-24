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
import org.springframework.stereotype.Service;

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
        ResultEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setScope(dto.getScope());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive());
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
        repository.deleteById(id);
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
        ResultEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }
}
