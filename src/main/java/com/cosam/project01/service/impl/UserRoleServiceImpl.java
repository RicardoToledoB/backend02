package com.cosam.project01.service.impl;
import com.cosam.project01.dto.UserRoleDTO;
import com.cosam.project01.dto.RoleDTO;
import com.cosam.project01.dto.UserDTO;
import com.cosam.project01.entity.UserRoleEntity;
import com.cosam.project01.entity.RoleEntity;
import com.cosam.project01.entity.UserEntity;
import com.cosam.project01.repository.UserRoleRepository;
import com.cosam.project01.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleServiceImpl implements IUserRoleService {

    @Autowired
    private UserRoleRepository repository;


    private UserRoleDTO mapToDTO(UserRoleEntity entity) {
        return UserRoleDTO.builder()
                .id(entity.getId())
                .user(mapToUserDTO(entity.getUser()))
                .role(mapToRoleDTO(entity.getRole()))
                .active(entity.getActive())
                .assignedByUser(entity.getAssignedByUser() != null ? mapToUserDTO(entity.getAssignedByUser()) : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserRoleEntity mapToEntity(UserRoleDTO dto) {
        return UserRoleEntity.builder()
                .id(dto.getId())
                .user(mapToUserEntity(dto.getUser()))
                .role(mapToRoleEntity(dto.getRole()))
                .active(dto.getActive())
                .assignedByUser(dto.getAssignedByUser() != null ? mapToUserEntity(dto.getAssignedByUser()) : null)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    private UserDTO mapToUserDTO(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .secondName(entity.getSecondName())
                .firstLastName(entity.getFirstLastName())
                .secondLastName(entity.getSecondLastName())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .password(null)
                .rut(entity.getRut())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserEntity mapToUserEntity(UserDTO dto) {
        return UserEntity.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .secondName(dto.getSecondName())
                .firstLastName(dto.getFirstLastName())
                .secondLastName(dto.getSecondLastName())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .rut(dto.getRut())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private RoleDTO mapToRoleDTO(RoleEntity entity) {
        return RoleDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private RoleEntity mapToRoleEntity(RoleDTO dto) {
        return RoleEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .active(dto.getActive())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    public UserRoleDTO create(UserRoleDTO dto) {
        UserRoleEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public UserRoleDTO update(Integer id, UserRoleDTO dto) {
        UserRoleEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
                entity.setUser(mapToUserEntity(dto.getUser()));
                entity.setRole(mapToRoleEntity(dto.getRole()));
                entity.setActive(dto.getActive());
                entity.setAssignedByUser(dto.getAssignedByUser() != null ? mapToUserEntity(dto.getAssignedByUser()) : null);
        return mapToDTO(repository.save(entity));
    }

    @Override
    public UserRoleDTO getById(Integer id) {
        UserRoleEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<UserRoleDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<UserRoleDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<UserRoleDTO> getAllPaginated(Integer id, Pageable pageable) {
        return repository.search(id, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<UserRoleDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<UserRoleDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<UserRoleDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        UserRoleEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    public List<UserRoleDTO> getRolesByUser(Integer userId) {
        return repository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteByUserId(Integer userId) {
        repository.softDeleteByUserId(userId);
    }

    @Transactional
    public void deleteByUserAndRole(Integer userId, Integer roleId) {
        UserRoleEntity entity = repository.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new RuntimeException("La relación usuario-rol no existe o ya fue eliminada."));
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }
}
