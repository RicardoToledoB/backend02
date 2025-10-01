package com.cosam.project01.service.impl;

import com.cosam.project01.dto.UserDTO;
import com.cosam.project01.dto.UserDTO;
import com.cosam.project01.entity.UserEntity;
import com.cosam.project01.entity.UserEntity;
import com.cosam.project01.repository.CommuneRepository;
import com.cosam.project01.repository.UserRepository;
import com.cosam.project01.service.ICommuneService;
import com.cosam.project01.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements IUserService {
    
    
    
    /*
    private String firstName;
    private String secondName;
    private String firstLastName;
    private String secondLastName;
    private String email;
    private String username;
    private String password;
    private String rut;
   
     */

    @Autowired
    private UserRepository repository;


    private UserDTO mapToDTO(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .secondName(entity.getSecondName())
                .firstLastName(entity.getFirstLastName())
                .secondLastName(entity.getSecondLastName())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .rut(entity.getRut())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserEntity mapToEntity(UserDTO dto) {
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

    public UserDTO create(UserDTO dto) {
        UserEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public UserDTO update(Integer id, UserDTO dto) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
                entity.setFirstName(dto.getFirstName());
                entity.setSecondName(dto.getSecondName());
                entity.setFirstLastName(dto.getFirstLastName());
                entity.setSecondLastName(dto.getSecondLastName());
                entity.setEmail(dto.getEmail());
                entity.setUsername(dto.getUsername());
                entity.setPassword(dto.getPassword());
                entity.setRut(dto.getRut());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public UserDTO getById(Integer id) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<UserDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> findByEmail(String name) {
        return repository.findByEmail(name).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }


    public Page<UserDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<UserDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<UserDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<UserDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<UserDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        UserEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }
}
