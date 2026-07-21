package com.cosam.project01.service.impl;

import com.cosam.project01.dto.*;
import com.cosam.project01.entity.*;
import com.cosam.project01.repository.ConvPrevRepository;
import com.cosam.project01.repository.PostulantRepository;
import com.cosam.project01.service.IPostulantService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostulantServiceImpl implements IPostulantService {

    @Autowired
    private PostulantRepository repository;

    @Autowired
    private ConvPrevRepository convPrevRepository;

    private PostulantDTO mapToDTO(PostulantEntity entity) {
        ConvPrevEntity convPrev = entity.getConvPrev();
        ConvPrevDTO convPrevDTO = mapToConvPrevDTO(convPrev);

        return PostulantDTO.builder()
                .id(entity.getId())
                .user(mapToUserDTO(entity.getUser()))
                .commune(mapToCommuneDTO(entity.getCommune()))
                .address(entity.getAddress())
                .sex(mapToSexDTO(entity.getSex()))
                .convPrev(convPrevDTO)
                .convPrevId(safeConvPrevId(convPrev))
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

    private PostulantEntity mapToEntity(PostulantDTO dto) {
        return PostulantEntity.builder()
                .id(dto.getId())
                .user(mapToUserEntity(dto.getUser()))
                .commune(mapToCommuneEntity(dto.getCommune()))
                .address(dto.getAddress())
                .sex(mapToSexEntity(dto.getSex()))
                .convPrev(resolveConvPrev(dto))
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
        if (dto == null || dto.getId() == null) {
            return null;
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
        if (entity == null) {
            return null;
        }
        return SexDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private CommuneDTO mapToCommuneDTO(CommuneEntity entity) {
        if (entity == null) {
            return null;
        }
        return CommuneDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private CommuneEntity mapToCommuneEntity(CommuneDTO dto) {
        if (dto == null || dto.getId() == null) {
            return null;
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
        if (entity == null) {
            return null;
        }
        return UserDTO.builder()
                .id(entity.getId())
                .rut(entity.getRut())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserEntity mapToUserEntity(UserDTO dto) {
        if (dto == null || dto.getId() == null) {
            return null;
        }
        return UserEntity.builder()
                .id(dto.getId())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private Integer safeConvPrevId(ConvPrevEntity entity) {
        try {
            return entity != null ? entity.getId() : null;
        } catch (EntityNotFoundException ex) {
            return null;
        }
    }

    private ConvPrevDTO mapToConvPrevDTO(ConvPrevEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            IntPrevEntity intPrev = entity.getIntPrev();
            IntPrevDTO intPrevDTO = null;
            if (intPrev != null) {
                intPrevDTO = IntPrevDTO.builder()
                        .id(intPrev.getId())
                        .code(intPrev.getCode())
                        .name(intPrev.getName())
                        .description(intPrev.getDescription())
                        .active(intPrev.getActive())
                        .createdAt(intPrev.getCreatedAt())
                        .updatedAt(intPrev.getUpdatedAt())
                        .deletedAt(intPrev.getDeletedAt())
                        .build();
            }

            return ConvPrevDTO.builder()
                    .id(entity.getId())
                    .code(entity.getCode())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .active(entity.getActive())
                    .intPrevId(intPrev != null ? intPrev.getId() : null)
                    .intPrevCode(intPrev != null ? intPrev.getCode() : null)
                    .intPrevName(intPrev != null ? intPrev.getName() : null)
                    .intPrev(intPrevDTO)
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .deletedAt(entity.getDeletedAt())
                    .build();
        } catch (EntityNotFoundException ex) {
            return null;
        }
    }

    private Integer resolveConvPrevId(PostulantDTO dto) {
        if (dto == null) {
            return null;
        }
        if (dto.getConvPrevId() != null) {
            return dto.getConvPrevId();
        }
        if (dto.getConvPrev() != null && dto.getConvPrev().getId() != null) {
            return dto.getConvPrev().getId();
        }
        return null;
    }

    private ConvPrevEntity resolveConvPrev(PostulantDTO dto) {
        Integer convPrevId = resolveConvPrevId(dto);
        if (convPrevId == null) {
            return null;
        }
        return convPrevRepository.findById(convPrevId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Convenio previsional no encontrado: " + convPrevId));
    }

    public PostulantDTO create(PostulantDTO dto) {
        PostulantEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public PostulantDTO update(Integer id, PostulantDTO dto) {
        PostulantEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulante no encontrado: " + id));

        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setRut(dto.getRut());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setBirthdate(dto.getBirthdate());
        entity.setUser(mapToUserEntity(dto.getUser()));
        entity.setCommune(mapToCommuneEntity(dto.getCommune()));
        entity.setFirstLastName(dto.getFirstLastName());
        entity.setSecondLastName(dto.getSecondLastName());
        entity.setSex(mapToSexEntity(dto.getSex()));

        if (dto.getConvPrevId() != null || dto.getConvPrev() != null) {
            entity.setConvPrev(resolveConvPrev(dto));
        }

        return mapToDTO(repository.save(entity));
    }

    @Override
    public PostulantDTO getById(Integer id) {
        PostulantEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulante no encontrado: " + id));
        return mapToDTO(entity);
    }

    @Override
    public List<PostulantDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<PostulantDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<PostulantDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }

    public Page<PostulantDTO> searchByRut(String rut, Pageable pageable) {
        return repository.searchByRutNormalized(rut == null ? "" : rut.trim(), pageable).map(this::mapToDTO);
    }

    /*Listar communas activas*/
    public List<PostulantDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostulantDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostulantDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        PostulantEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulante no encontrado: " + id));
        entity.setDeletedAt(null);
        repository.save(entity);
    }
}
