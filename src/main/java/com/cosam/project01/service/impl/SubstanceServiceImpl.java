package com.cosam.project01.service.impl;

import com.cosam.project01.dto.SubstanceDTO;
import com.cosam.project01.dto.SubstanceDTO;
import com.cosam.project01.entity.SubstanceEntity;
import com.cosam.project01.entity.SubstanceEntity;
import com.cosam.project01.repository.SubstanceRepository;
import com.cosam.project01.service.ISubstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubstanceServiceImpl implements ISubstanceService {


    @Autowired
    private SubstanceRepository repository;


    private SubstanceDTO mapToDTO(SubstanceEntity entity) {
        return SubstanceDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .level(entity.getLevel())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private SubstanceEntity mapToEntity(SubstanceDTO dto) {
        return SubstanceEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .level(dto.getLevel())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    public SubstanceDTO create(SubstanceDTO dto) {
        SubstanceEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public SubstanceDTO update(Integer id, SubstanceDTO dto) {
        SubstanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setName(dto.getName());
        entity.setLevel(dto.getLevel());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public SubstanceDTO getById(Integer id) {
        SubstanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<SubstanceDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }


    public Page<SubstanceDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<SubstanceDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<SubstanceDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<SubstanceDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<SubstanceDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        SubstanceEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }
}
