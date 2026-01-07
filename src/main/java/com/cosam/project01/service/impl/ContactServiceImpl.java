package com.cosam.project01.service.impl;

import com.cosam.project01.dto.*;
import com.cosam.project01.entity.*;
import com.cosam.project01.repository.ContactRepository;
import com.cosam.project01.service.IContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements IContactService {

    @Autowired
    private ContactRepository repository;

    /* =======================
       MAP ENTITY -> DTO
       ======================= */
    private ContactDTO mapToDTO(ContactEntity entity) {
        if (entity == null) return null;

        return ContactDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .cellphone(entity.getCellphone())
                .description(entity.getDescription())

                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    /* =======================
       MAP DTO -> ENTITY (CREATE)
       ======================= */
    private ContactEntity mapToEntity(ContactDTO dto) {



        return ContactEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .cellphone(dto.getCellphone())
                .description(dto.getDescription())
                // 👉 SOLO referencia por ID

                .build();
    }

    /* =======================
       POSTULANT (SOLO LECTURA)
       ======================= */
    private PostulantDTO mapToPostulantDTO(PostulantEntity entity) {
        if (entity == null) return null;

        return PostulantDTO.builder()
                .id(entity.getId())
                .user(entity.getUser() != null ? mapToUserDTO(entity.getUser()) : null)
                .commune(entity.getCommune() != null ? mapToCommuneDTO(entity.getCommune()) : null)
                .address(entity.getAddress())
                .sex(entity.getSex() != null ? mapToSexDTO(entity.getSex()) : null)
                .convPrev(entity.getConvPrev() != null ? mapToConvPrevDTO(entity.getConvPrev()) : null)
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

    /* =======================
       MAPPERS SIMPLES (DTO)
       ======================= */
    private UserDTO mapToUserDTO(UserEntity entity) {
        if (entity == null) return null;

        return UserDTO.builder()
                .id(entity.getId())
                .rut(entity.getRut())
                .email(entity.getEmail())
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

    private ConvPrevDTO mapToConvPrevDTO(ConvPrevEntity entity) {
        if (entity == null) return null;

        return ConvPrevDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .intPrev(entity.getIntPrev() != null ? mapToIntPrevDTO(entity.getIntPrev()) : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private IntPrevDTO mapToIntPrevDTO(IntPrevEntity entity) {
        if (entity == null) return null;

        return IntPrevDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    /* =======================
       CRUD
       ======================= */
    @Override
    public ContactDTO create(ContactDTO dto) {
        ContactEntity entity = mapToEntity(dto);
        return mapToDTO(repository.save(entity));
    }

    @Override
    public ContactDTO update(Integer id, ContactDTO dto) {
        ContactEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setCellphone(dto.getCellphone());
        entity.setDescription(dto.getDescription());



        return mapToDTO(repository.save(entity));
    }

    @Override
    public ContactDTO getById(Integer id) {
        return repository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public List<ContactDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<ContactDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable).map(this::mapToDTO);
    }

    public Page<ContactDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }

    public List<ContactDTO> listAll() {
        return repository.findAllIncludingDeleted()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ContactDTO> listActive() {
        return repository.findAllActive()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ContactDTO> listDeleted() {
        return repository.findAllDeleted()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        ContactEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }
}
