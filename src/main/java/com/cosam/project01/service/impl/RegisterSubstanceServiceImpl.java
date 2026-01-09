package com.cosam.project01.service.impl;

import com.cosam.project01.dto.*;
import com.cosam.project01.entity.*;
import com.cosam.project01.repository.RegisterSubstanceRepository;
import com.cosam.project01.service.IRegisterSubstanceService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegisterSubstanceServiceImpl implements IRegisterSubstanceService {

    @Autowired
    private RegisterSubstanceRepository repository;

    /* =========================
       MAPPERS (NULL-SAFE)
       ========================= */

    private RegisterSubstanceDTO mapToDTO(RegisterSubstanceEntity entity) {
        if (entity == null) return null;

        return RegisterSubstanceDTO.builder()
                .id(entity.getId())
                .register(mapToRegisterDTO(entity.getRegister()))
                .substance(mapToSubstanceDTO(entity.getSubstance()))
                .level(entity.getLevel())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private RegisterSubstanceEntity mapToEntity(RegisterSubstanceDTO dto) {
        if (dto == null) return null;

        return RegisterSubstanceEntity.builder()
                .id(dto.getId())
                .register(mapToRegisterEntity(dto.getRegister()))
                .substance(mapToSubstanceEntity(dto.getSubstance()))
                .level(dto.getLevel())
                // NO seteamos createdAt/updatedAt: lo manejan @PrePersist/@PreUpdate
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private RegisterDTO mapToRegisterDTO(RegisterEntity entity) {
        if (entity == null) return null;

        return RegisterDTO.builder()
                .id(entity.getId())
                .postulant(mapToPostulantDTO(entity.getPostulant()))
                .contactType(mapToContactTypeDTO(entity.getContactType()))
                .sender(mapToSenderDTO(entity.getSender()))
                .diverter(mapToDiverterDTO(entity.getDiverter()))
                .program(mapToProgramDTO(entity.getProgram()))
                .user(mapToUserDTO(entity.getUser()))
                .date_attention(entity.getDate_attention())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private RegisterEntity mapToRegisterEntity(RegisterDTO dto) {
        if (dto == null) return null;

        // si viene solo el ID (caso recomendado para POST/PUT), crea referencia mínima
        if (dto.getId() != null &&
                dto.getPostulant() == null &&
                dto.getContactType() == null &&
                dto.getSender() == null &&
                dto.getDiverter() == null &&
                dto.getProgram() == null &&
                dto.getUser() == null) {

            return RegisterEntity.builder()
                    .id(dto.getId())
                    .build();
        }

        return RegisterEntity.builder()
                .id(dto.getId())
                .postulant(dto.getPostulant() != null ? mapToPostulantEntity(dto.getPostulant()) : null)
                .contactType(dto.getContactType() != null ? mapToContactTypeEntity(dto.getContactType()) : null)
                .sender(dto.getSender() != null ? mapToSenderEntity(dto.getSender()) : null)
                .diverter(dto.getDiverter() != null ? mapToDiverterEntity(dto.getDiverter()) : null)
                .program(dto.getProgram() != null ? mapToProgramEntity(dto.getProgram()) : null)
                .user(dto.getUser() != null ? mapToUserEntity(dto.getUser()) : null)
                .date_attention(dto.getDate_attention())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private PostulantDTO mapToPostulantDTO(PostulantEntity entity) {
        if (entity == null) return null;

        return PostulantDTO.builder()
                .id(entity.getId())
                .user(mapToUserDTO(entity.getUser()))
                .commune(mapToCommuneDTO(entity.getCommune()))
                .address(entity.getAddress())
                .sex(mapToSexDTO(entity.getSex()))
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .rut(entity.getRut())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .secondLastName(entity.getSecondLastName())
                .firstLastName(entity.getFirstLastName())
                .birthdate(entity.getBirthdate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private PostulantEntity mapToPostulantEntity(PostulantDTO dto) {
        if (dto == null) return null;

        // si viene solo el ID, referencia mínima
        if (dto.getId() != null &&
                dto.getUser() == null &&
                dto.getCommune() == null &&
                dto.getSex() == null) {
            return PostulantEntity.builder().id(dto.getId()).build();
        }

        return PostulantEntity.builder()
                .id(dto.getId())
                .user(mapToUserEntity(dto.getUser()))
                .commune(mapToCommuneEntity(dto.getCommune()))
                .address(dto.getAddress())
                .sex(mapToSexEntity(dto.getSex()))
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .rut(dto.getRut())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .secondLastName(dto.getSecondLastName())
                .firstLastName(dto.getFirstLastName())
                .birthdate(dto.getBirthdate())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private SexEntity mapToSexEntity(SexDTO dto) {
        if (dto == null) return null;

        // referencia mínima si viene solo ID
        if (dto.getId() != null && dto.getName() == null) {
            return SexEntity.builder().id(dto.getId()).build();
        }

        return SexEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private SexDTO mapToSexDTO(SexEntity entity) {
        if (entity == null) return null;

        return SexDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private CommuneDTO mapToCommuneDTO(CommuneEntity entity) {
        if (entity == null) return null;

        return CommuneDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private CommuneEntity mapToCommuneEntity(CommuneDTO dto) {
        if (dto == null) return null;

        // referencia mínima si viene solo ID
        if (dto.getId() != null && dto.getName() == null) {
            return CommuneEntity.builder().id(dto.getId()).build();
        }

        return CommuneEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private UserDTO mapToUserDTO(UserEntity entity) {
        if (entity == null) return null;

        return UserDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .secondName(entity.getSecondName())
                .firstLastName(entity.getFirstLastName())
                .secondLastName(entity.getSecondLastName())
                .email(entity.getEmail())
                .username(entity.getUsername())
                // IMPORTANTE: no exponer password en respuestas
                // .password(entity.getPassword())
                .rut(entity.getRut())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserEntity mapToUserEntity(UserDTO dto) {
        if (dto == null) return null;

        // referencia mínima si viene solo ID/username/email (ajusta si tu UserEntity requiere algo)
        if (dto.getId() != null &&
                dto.getFirstName() == null &&
                dto.getUsername() == null &&
                dto.getEmail() == null) {
            return UserEntity.builder().id(dto.getId()).build();
        }

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

    private ProgramDTO mapToProgramDTO(ProgramEntity entity) {
        if (entity == null) return null;

        return ProgramDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private ProgramEntity mapToProgramEntity(ProgramDTO dto) {
        if (dto == null) return null;

        // referencia mínima si viene solo ID
        if (dto.getId() != null && dto.getName() == null) {
            return ProgramEntity.builder().id(dto.getId()).build();
        }

        return ProgramEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private ContactTypeDTO mapToContactTypeDTO(ContactTypeEntity entity) {
        if (entity == null) return null;

        return ContactTypeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private ContactTypeEntity mapToContactTypeEntity(ContactTypeDTO dto) {
        if (dto == null) return null;

        // referencia mínima si viene solo ID
        if (dto.getId() != null && dto.getName() == null) {
            return ContactTypeEntity.builder().id(dto.getId()).build();
        }

        return ContactTypeEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private SenderDTO mapToSenderDTO(SenderEntity entity) {
        if (entity == null) return null;

        return SenderDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private SenderEntity mapToSenderEntity(SenderDTO dto) {
        if (dto == null) return null;

        // referencia mínima si viene solo ID
        if (dto.getId() != null && dto.getName() == null) {
            return SenderEntity.builder().id(dto.getId()).build();
        }

        return SenderEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private DiverterDTO mapToDiverterDTO(DiverterEntity entity) {
        if (entity == null) return null;

        return DiverterDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private DiverterEntity mapToDiverterEntity(DiverterDTO dto) {
        if (dto == null) return null;

        // referencia mínima si viene solo ID
        if (dto.getId() != null && dto.getName() == null) {
            return DiverterEntity.builder().id(dto.getId()).build();
        }

        return DiverterEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private SubstanceDTO mapToSubstanceDTO(SubstanceEntity entity) {
        if (entity == null) return null;

        return SubstanceDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    private SubstanceEntity mapToSubstanceEntity(SubstanceDTO dto) {
        if (dto == null) return null;

        // solo ID → referencia mínima
        if (dto.getId() != null && dto.getName() == null) {
            return SubstanceEntity.builder()
                    .id(dto.getId())
                    .build();
        }

        return SubstanceEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    /* =========================
       CRUD
       ========================= */

    public RegisterSubstanceDTO create(RegisterSubstanceDTO dto) {
        RegisterSubstanceEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public RegisterSubstanceDTO update(Integer id, RegisterSubstanceDTO dto) {
        RegisterSubstanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("RegisterSubstance not found"));

        entity.setRegister(mapToRegisterEntity(dto != null ? dto.getRegister() : null));
        entity.setSubstance(mapToSubstanceEntity(dto != null ? dto.getSubstance() : null));
        entity.setLevel(dto != null ? dto.getLevel() : null);

        return mapToDTO(repository.save(entity));
    }

    @Override
    public RegisterSubstanceDTO getById(Integer id) {
        RegisterSubstanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("RegisterSubstance not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<RegisterSubstanceDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<RegisterSubstanceDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable).map(this::mapToDTO);
    }

    public Page<RegisterSubstanceDTO> getAllPaginated(Integer id, Pageable pageable) {
        return repository.search(id, pageable).map(this::mapToDTO);
    }

    public List<RegisterSubstanceDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<RegisterSubstanceDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<RegisterSubstanceDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        RegisterSubstanceEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("RegisterSubstance not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    public Page<RegisterSubstanceDTO> searchByRegisterId(Integer registerId, Pageable pageable) {
        return repository.searchByRegisterId(registerId, pageable).map(this::mapToDTO);
    }

    @Transactional
    public int deleteByRegisterId(Integer registerId) {
        return repository.softDeleteByRegisterId(registerId);
    }
}
