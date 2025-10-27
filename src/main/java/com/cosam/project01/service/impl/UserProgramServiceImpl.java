package com.cosam.project01.service.impl;

import com.cosam.project01.dto.ProgramDTO;
import com.cosam.project01.dto.UserDTO;
import com.cosam.project01.dto.UserProgramDTO;
import com.cosam.project01.dto.UserRoleDTO;
import com.cosam.project01.entity.ProgramEntity;
import com.cosam.project01.entity.UserEntity;
import com.cosam.project01.entity.UserProgramEntity;
import com.cosam.project01.repository.ProgramRepository;
import com.cosam.project01.repository.UserProgramRepository;
import com.cosam.project01.repository.UserRepository;
import com.cosam.project01.service.IUserProgramService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProgramServiceImpl implements IUserProgramService {

    @Autowired
    private UserProgramRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgramRepository programRepository;

    private UserDTO mapUserToDTO(UserEntity user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .firstLastName(user.getFirstLastName())
                .secondLastName(user.getSecondLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .rut(user.getRut())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    private ProgramDTO mapProgramToDTO(ProgramEntity program) {
        return ProgramDTO.builder()
                .id(program.getId())
                .name(program.getName())
                .createdAt(program.getCreatedAt())
                .updatedAt(program.getUpdatedAt())
                .deletedAt(program.getDeletedAt())
                .build();
    }

    private UserProgramDTO mapToDTO(UserProgramEntity entity) {
        return UserProgramDTO.builder()
                .id(entity.getId())
                .user(mapUserToDTO(entity.getUser()))
                .program(mapProgramToDTO(entity.getProgram()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserProgramEntity mapToEntity(UserProgramDTO dto) {
        return UserProgramEntity.builder()
                .id(dto.getId())
                .user(userRepository.findById(dto.getUser().getId())
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .program(programRepository.findById(dto.getProgram().getId())
                        .orElseThrow(() -> new RuntimeException("Program not found")))
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    @Override
    public UserProgramDTO create(UserProgramDTO dto) {
        UserProgramEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public UserProgramDTO update(Integer id, UserProgramDTO dto) {
        UserProgramEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relation not found"));
        entity.setUser(userRepository.findById(dto.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        entity.setProgram(programRepository.findById(dto.getProgram().getId())
                .orElseThrow(() -> new RuntimeException("Program not found")));
        return mapToDTO(repository.save(entity));
    }

    @Override
    public UserProgramDTO getById(Integer id) {
        return repository.findAnyById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Relation not found"));
    }

    @Override
    public List<UserProgramDTO> getAll() {
        return repository.findAll()
                .stream().map(this::mapToDTO)
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

    public List<UserProgramDTO> listAll() {
        return repository.findAllIncludingDeleted()
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserProgramDTO> listActive() {
        return repository.findAllActive()
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserProgramDTO> listDeleted() {
        return repository.findAllDeleted()
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        UserProgramEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Relation not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    public List<UserProgramDTO> getUserProgramByUser(Integer userId) {
        return repository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteByUserId(Integer userId) {
        repository.deleteByUserId(userId);
    }

    @Transactional
    public void deleteByUserAndProgram(Integer userId, Integer programId) {
        UserProgramEntity entity = repository.findByUserIdAndProgramId(userId, programId)
                .orElseThrow(() -> new RuntimeException("La relación usuario-programa no existe o ya fue eliminada."));
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }

}
