package com.cosam.project01.service.impl;

import com.cosam.project01.dto.UserProgramDTO;
import com.cosam.project01.entity.UserProgramEntity;
import com.cosam.project01.repository.UserProgramRepository;
import com.cosam.project01.service.IUserProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProgramServiceImpl implements IUserProgramService {

    @Autowired
    private UserProgramRepository repository;


    private UserProgramDTO mapToDTO(UserProgramEntity entity) {
        return UserProgramDTO.builder()
                .id(entity.getId())
                .user(entity.getUser())
                .program(entity.getProgram())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserProgramEntity mapToEntity(UserProgramDTO dto) {
        return UserProgramEntity.builder()
                .id(dto.getId())
                .user(dto.getUser())
                .program(dto.getProgram())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    public UserProgramDTO create(UserProgramDTO dto) {
        UserProgramEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public UserProgramDTO update(Integer id, UserProgramDTO dto) {
        UserProgramEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setUser(dto.getUser());
        entity.setProgram(dto.getProgram());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public UserProgramDTO getById(Integer id) {
        UserProgramEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<UserProgramDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public Page<UserProgramDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    /*Listar communas activas*/
    public List<UserProgramDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<UserProgramDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<UserProgramDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        UserProgramEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }
}
