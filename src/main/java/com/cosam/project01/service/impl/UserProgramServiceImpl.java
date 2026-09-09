package com.cosam.project01.service.impl;

import com.cosam.project01.dto.ProgramDTO;
import com.cosam.project01.dto.UserDTO;
import com.cosam.project01.dto.UserProgramDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
        if (user == null) return null;
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
        if (program == null) return null;
        return ProgramDTO.builder()
                .id(program.getId())
                .name(program.getName())
                .populationTypeId(program.getPopulationType() != null ? program.getPopulationType().getId() : null)
                .modalityId(program.getModality() != null ? program.getModality().getId() : null)
                .planId(program.getPlan() != null ? program.getPlan().getId() : null)
                .regionId(program.getRegion() != null ? program.getRegion().getId() : null)
                .cityId(program.getCity() != null ? program.getCity().getId() : null)
                .address(program.getAddress())
                .phone(program.getPhone())
                .email(program.getEmail())
                .description(program.getDescription())
                .active(program.getActive())
                .createdAt(program.getCreatedAt())
                .updatedAt(program.getUpdatedAt())
                .deletedAt(program.getDeletedAt())
                .build();
    }

    private UserProgramDTO mapToDTO(UserProgramEntity entity) {
        ProgramEntity program = entity.getProgram();
        boolean transversal = program == null;
        return UserProgramDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .programId(program != null ? program.getId() : null)
                .user(mapUserToDTO(entity.getUser()))
                .program(mapProgramToDTO(program))
                .transversal(transversal)
                .communicationScope(transversal ? "TRANSVERSAL" : "PROGRAM")
                .isActive(entity.getIsActive())
                .isSupervisor(entity.getIsSupervisor())
                .canReceiveReferences(entity.getCanReceiveReferences())
                .canManageCommunications(entity.getCanManageCommunications())
                .canReceiveCitations(entity.getCanReceiveCitations())
                .canReceiveAttendances(entity.getCanReceiveAttendances())
                .canReceiveFeedback(entity.getCanReceiveFeedback())
                .canReceiveClosures(entity.getCanReceiveClosures())
                .canReceiveDocuments(entity.getCanReceiveDocuments())
                .canReceiveObservations(entity.getCanReceiveObservations())
                .canManageDemands(entity.getCanManageDemands())
                .canViewDashboard(entity.getCanViewDashboard())
                .roleInProgram(entity.getRoleInProgram())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserProgramEntity mapToEntity(UserProgramDTO dto) {
        return UserProgramEntity.builder()
                .id(dto.getId())
                .user(resolveUser(dto))
                .program(resolveProgramForCreate(dto))
                .isActive(valueOrDefault(dto.getIsActive(), true))
                .isSupervisor(valueOrDefault(dto.getIsSupervisor(), false))
                .canReceiveReferences(valueOrDefault(dto.getCanReceiveReferences(), false))
                .canManageCommunications(valueOrDefault(dto.getCanManageCommunications(), false))
                .canReceiveCitations(valueOrDefault(dto.getCanReceiveCitations(), false))
                .canReceiveAttendances(valueOrDefault(dto.getCanReceiveAttendances(), false))
                .canReceiveFeedback(valueOrDefault(dto.getCanReceiveFeedback(), false))
                .canReceiveClosures(valueOrDefault(dto.getCanReceiveClosures(), false))
                .canReceiveDocuments(valueOrDefault(dto.getCanReceiveDocuments(), false))
                .canReceiveObservations(valueOrDefault(dto.getCanReceiveObservations(), false))
                .canManageDemands(valueOrDefault(dto.getCanManageDemands(), false))
                .canViewDashboard(valueOrDefault(dto.getCanViewDashboard(), false))
                .roleInProgram(dto.getRoleInProgram())
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

        if (hasUserReference(dto)) {
            entity.setUser(resolveUser(dto));
        }

        if (Boolean.TRUE.equals(dto.getTransversal())) {
            entity.setProgram(null);
        } else {
            Integer programId = extractProgramId(dto);
            if (programId != null) {
                entity.setProgram(programRepository.findById(programId)
                        .orElseThrow(() -> new RuntimeException("Program not found")));
            }
        }

        entity.setIsActive(valueOrExisting(dto.getIsActive(), entity.getIsActive()));
        entity.setIsSupervisor(valueOrExisting(dto.getIsSupervisor(), entity.getIsSupervisor()));
        entity.setCanReceiveReferences(valueOrExisting(dto.getCanReceiveReferences(), entity.getCanReceiveReferences()));
        entity.setCanManageCommunications(valueOrExisting(dto.getCanManageCommunications(), entity.getCanManageCommunications()));
        entity.setCanReceiveCitations(valueOrExisting(dto.getCanReceiveCitations(), entity.getCanReceiveCitations()));
        entity.setCanReceiveAttendances(valueOrExisting(dto.getCanReceiveAttendances(), entity.getCanReceiveAttendances()));
        entity.setCanReceiveFeedback(valueOrExisting(dto.getCanReceiveFeedback(), entity.getCanReceiveFeedback()));
        entity.setCanReceiveClosures(valueOrExisting(dto.getCanReceiveClosures(), entity.getCanReceiveClosures()));
        entity.setCanReceiveDocuments(valueOrExisting(dto.getCanReceiveDocuments(), entity.getCanReceiveDocuments()));
        entity.setCanReceiveObservations(valueOrExisting(dto.getCanReceiveObservations(), entity.getCanReceiveObservations()));
        entity.setCanManageDemands(valueOrExisting(dto.getCanManageDemands(), entity.getCanManageDemands()));
        entity.setCanViewDashboard(valueOrExisting(dto.getCanViewDashboard(), entity.getCanViewDashboard()));
        entity.setRoleInProgram(dto.getRoleInProgram());
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

    public List<UserProgramDTO> getCommunicationConfigurations(Integer programId, String communicationType) {
        String normalizedType = normalizeCommunicationType(communicationType, false);
        return repository.findActiveCommunicationConfigurations(programId).stream()
                .filter(entity -> matchesCommunicationType(entity, normalizedType))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserProgramDTO> getCommunicationRecipients(Integer programId, String communicationType) {
        String normalizedType = normalizeCommunicationType(communicationType, true);
        Map<Integer, UserProgramDTO> byUser = new LinkedHashMap<>();

        repository.findActiveCommunicationConfigurations(programId).stream()
                .filter(entity -> matchesCommunicationType(entity, normalizedType))
                .map(this::mapToDTO)
                .forEach(dto -> {
                    Integer userId = dto.getUserId();
                    if (userId == null) return;
                    UserProgramDTO previous = byUser.get(userId);
                    if (previous == null || Boolean.TRUE.equals(previous.getTransversal())) {
                        byUser.put(userId, dto);
                    }
                });

        return byUser.values().stream().collect(Collectors.toList());
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

    @Transactional
    public void deleteTransversalByUser(Integer userId) {
        UserProgramEntity entity = repository.findTransversalByUserId(userId)
                .orElseThrow(() -> new RuntimeException("La configuración transversal del usuario no existe o ya fue eliminada."));
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }

    private UserEntity resolveUser(UserProgramDTO dto) {
        Integer userId = dto.getUserId();
        if (userId == null && dto.getUser() != null) {
            userId = dto.getUser().getId();
        }
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar userId o user.id.");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean hasUserReference(UserProgramDTO dto) {
        return dto.getUserId() != null || (dto.getUser() != null && dto.getUser().getId() != null);
    }

    private ProgramEntity resolveProgramForCreate(UserProgramDTO dto) {
        Integer programId = extractProgramId(dto);
        if (programId == null) {
            return null;
        }
        return programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));
    }

    private Integer extractProgramId(UserProgramDTO dto) {
        if (dto.getProgramId() != null) return dto.getProgramId();
        if (dto.getProgram() != null) return dto.getProgram().getId();
        return null;
    }

    private String normalizeCommunicationType(String communicationType, boolean required) {
        if (communicationType == null || communicationType.isBlank()) {
            if (required) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar type para resolver destinatarios.");
            }
            return null;
        }

        String value = communicationType.trim().toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');

        return switch (value) {
            case "REFERENCE", "REFERENCES", "REFERENCIA", "REFERENCIAS" -> "REFERENCES";
            case "CITATION", "CITATIONS", "CITACION", "CITACIONES" -> "CITATIONS";
            case "ATTENDANCE", "ATTENDANCES", "ASISTENCIA", "ASISTENCIAS" -> "ATTENDANCES";
            case "FEEDBACK", "RETROALIMENTACION", "RETROALIMENTACIONES" -> "FEEDBACK";
            case "CLOSURE", "CLOSURES", "CIERRE", "CIERRES" -> "CLOSURES";
            case "DOCUMENT", "DOCUMENTS", "DOCUMENTO", "DOCUMENTOS" -> "DOCUMENTS";
            case "OBSERVATION", "OBSERVATIONS", "OBSERVACION", "OBSERVACIONES" -> "OBSERVATIONS";
            case "MANAGE_COMMUNICATIONS", "ADMINISTRAR_COMUNICACIONES", "ADMIN_COMUNICACIONES" -> "MANAGE_COMMUNICATIONS";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo de comunicación no soportado: " + communicationType);
        };
    }

    private boolean matchesCommunicationType(UserProgramEntity entity, String normalizedType) {
        if (normalizedType == null) return true;
        return switch (normalizedType) {
            case "REFERENCES" -> Boolean.TRUE.equals(entity.getCanReceiveReferences());
            case "CITATIONS" -> Boolean.TRUE.equals(entity.getCanReceiveCitations());
            case "ATTENDANCES" -> Boolean.TRUE.equals(entity.getCanReceiveAttendances());
            case "FEEDBACK" -> Boolean.TRUE.equals(entity.getCanReceiveFeedback());
            case "CLOSURES" -> Boolean.TRUE.equals(entity.getCanReceiveClosures());
            case "DOCUMENTS" -> Boolean.TRUE.equals(entity.getCanReceiveDocuments());
            case "OBSERVATIONS" -> Boolean.TRUE.equals(entity.getCanReceiveObservations());
            case "MANAGE_COMMUNICATIONS" -> Boolean.TRUE.equals(entity.getCanManageCommunications());
            default -> false;
        };
    }

    private Boolean valueOrDefault(Boolean value, Boolean defaultValue) {
        return value != null ? value : defaultValue;
    }

    private Boolean valueOrExisting(Boolean value, Boolean existingValue) {
        return value != null ? value : valueOrDefault(existingValue, false);
    }

}
