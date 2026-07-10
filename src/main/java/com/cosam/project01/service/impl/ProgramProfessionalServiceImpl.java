package com.cosam.project01.service.impl;

import com.cosam.project01.dto.ProgramDTO;
import com.cosam.project01.dto.ProgramProfessionalDTO;
import com.cosam.project01.dto.ProgramProfessionalProgramRelationDTO;
import com.cosam.project01.entity.ProfessionEntity;
import com.cosam.project01.entity.ProgramEntity;
import com.cosam.project01.entity.ProgramProfessionalEntity;
import com.cosam.project01.entity.ProgramProfessionalProgramEntity;
import com.cosam.project01.repository.ProfessionRepository;
import com.cosam.project01.repository.ProgramProfessionalProgramRepository;
import com.cosam.project01.repository.ProgramProfessionalRepository;
import com.cosam.project01.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramProfessionalServiceImpl {

    private final ProgramProfessionalRepository repository;
    private final ProgramProfessionalProgramRepository linkRepository;
    private final ProfessionRepository professionRepository;
    private final ProgramRepository programRepository;

    @Transactional
    public ProgramProfessionalDTO create(ProgramProfessionalDTO dto) {
        validate(dto);

        ProgramProfessionalEntity entity = ProgramProfessionalEntity.builder()
                .name(dto.getName().trim())
                .profession(resolveProfession(dto.getProfessionId()))
                .email(blankToNull(dto.getEmail()))
                .phone(blankToNull(dto.getPhone()))
                .observation(blankToNull(dto.getObservation()))
                .active(dto.getActive() == null ? true : dto.getActive())
                .programLinks(new ArrayList<>())
                .build();

        ProgramProfessionalEntity saved = repository.save(entity);
        syncPrograms(saved, dto.getProgramIds());
        return toDTO(repository.findById(saved.getId()).orElse(saved));
    }

    @Transactional(readOnly = true)
    public ProgramProfessionalDTO getById(Long id) {
        return toDTO(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facultativo no encontrado")));
    }

    @Transactional(readOnly = true)
    public ProgramProfessionalDTO findByIdIncludingDeleted(Long id) {
        return toDTO(repository.findAnyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facultativo no encontrado")));
    }

    @Transactional(readOnly = true)
    public List<ProgramProfessionalDTO> listActive() {
        return repository.findAllActive().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProgramProfessionalDTO> listAll() {
        return repository.findAllIncludingDeleted().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProgramProfessionalDTO> listDeleted() {
        return repository.findAllDeleted().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProgramProfessionalDTO> getAllPaginated(String q, Integer professionId, Integer programId, Pageable pageable) {
        return repository.search(q, professionId, programId, pageable).map(this::toDTO);
    }


    @Transactional(readOnly = true)
    public List<ProgramProfessionalProgramRelationDTO> listDeletedProgramRelations(Long professionalId) {
        repository.findAnyById(professionalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facultativo no encontrado"));

        return linkRepository.findAllByProfessionalIncludingDeleted(professionalId).stream()
                .filter(link -> link.getDeletedAt() != null)
                .map(this::toRelationDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProgramProfessionalDTO> listByProgram(Integer programId) {
        if (programId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "programId es obligatorio");
        }
        if (!programRepository.existsById(programId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Programa no encontrado");
        }
        return repository.findActiveByProgramId(programId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ProgramProfessionalDTO update(Long id, ProgramProfessionalDTO dto) {
        ProgramProfessionalEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facultativo no encontrado"));

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) entity.setName(dto.getName().trim());
        if (dto.getProfessionId() != null) entity.setProfession(resolveProfession(dto.getProfessionId()));
        if (dto.getEmail() != null) entity.setEmail(blankToNull(dto.getEmail()));
        if (dto.getPhone() != null) entity.setPhone(blankToNull(dto.getPhone()));
        if (dto.getObservation() != null) entity.setObservation(blankToNull(dto.getObservation()));
        if (dto.getActive() != null) entity.setActive(dto.getActive());

        ProgramProfessionalEntity saved = repository.save(entity);
        if (dto.getProgramIds() != null) {
            syncPrograms(saved, dto.getProgramIds());
        }
        return toDTO(repository.findById(saved.getId()).orElse(saved));
    }

    @Transactional
    public void delete(Long id) {
        ProgramProfessionalEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facultativo no encontrado"));
        entity.setActive(false);
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
        linkRepository.softDeleteAllPrograms(id, LocalDateTime.now());
    }

    @Transactional
    public void restore(Long id) {
        ProgramProfessionalEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facultativo no encontrado"));
        entity.setDeletedAt(null);
        entity.setActive(true);
        repository.save(entity);
        linkRepository.restoreAllPrograms(id);
    }

    private void validate(ProgramProfessionalDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body requerido");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name es obligatorio");
        }
        if (dto.getProfessionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "professionId es obligatorio");
        }
    }

    private ProfessionEntity resolveProfession(Integer professionId) {
        if (professionId == null) return null;
        return professionRepository.findById(professionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesión no encontrada"));
    }

    private ProgramEntity resolveProgram(Integer programId) {
        if (programId == null) return null;
        return programRepository.findById(programId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Programa no encontrado: " + programId));
    }

    private void syncPrograms(ProgramProfessionalEntity professional, List<Integer> requestedProgramIds) {
        List<Integer> normalizedIds = normalizeProgramIds(requestedProgramIds);

        if (normalizedIds.isEmpty()) {
            linkRepository.softDeleteAllPrograms(professional.getId(), LocalDateTime.now());
            return;
        }

        linkRepository.softDeleteMissingPrograms(professional.getId(), normalizedIds, LocalDateTime.now());

        for (Integer programId : normalizedIds) {
            ProgramEntity program = resolveProgram(programId);
            ProgramProfessionalProgramEntity link = linkRepository
                    .findAnyByProfessionalAndProgram(professional.getId(), programId)
                    .orElse(null);

            if (link == null) {
                link = ProgramProfessionalProgramEntity.builder()
                        .programProfessional(professional)
                        .program(program)
                        .build();
            } else {
                link.setDeletedAt(null);
                link.setProgramProfessional(professional);
                link.setProgram(program);
            }
            linkRepository.save(link);
        }
    }

    private List<Integer> normalizeProgramIds(List<Integer> programIds) {
        if (programIds == null) return List.of();
        Set<Integer> unique = new LinkedHashSet<>();
        for (Integer id : programIds) {
            if (id != null) unique.add(id);
        }
        return new ArrayList<>(unique);
    }

    private ProgramProfessionalDTO toDTO(ProgramProfessionalEntity entity) {
        ProfessionEntity profession = entity.getProfession();
        List<ProgramProfessionalProgramEntity> links = linkRepository.findActiveByProfessionalId(entity.getId());
        List<Integer> programIds = links.stream()
                .map(link -> link.getProgram().getId())
                .collect(Collectors.toList());
        List<ProgramDTO> programs = links.stream()
                .map(link -> toProgramDTO(link.getProgram()))
                .collect(Collectors.toList());

        return ProgramProfessionalDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .professionId(profession != null ? profession.getId() : null)
                .professionCode(profession != null ? profession.getCode() : null)
                .professionName(profession != null ? profession.getName() : null)
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .observation(entity.getObservation())
                .active(entity.getActive())
                .programIds(programIds)
                .programs(programs)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }


    private ProgramProfessionalProgramRelationDTO toRelationDTO(ProgramProfessionalProgramEntity link) {
        ProgramEntity program = link.getProgram();
        return ProgramProfessionalProgramRelationDTO.builder()
                .id(link.getId())
                .programProfessionalId(link.getProgramProfessional() != null ? link.getProgramProfessional().getId() : null)
                .programId(program != null ? program.getId() : null)
                .programName(program != null ? program.getName() : null)
                .active(link.getDeletedAt() == null)
                .createdAt(link.getCreatedAt())
                .deletedAt(link.getDeletedAt())
                .build();
    }

    private ProgramDTO toProgramDTO(ProgramEntity entity) {
        return ProgramDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .populationTypeId(entity.getPopulationType() != null ? entity.getPopulationType().getId() : null)
                .modalityId(entity.getModality() != null ? entity.getModality().getId() : null)
                .planId(entity.getPlan() != null ? entity.getPlan().getId() : null)
                .regionId(entity.getRegion() != null ? entity.getRegion().getId() : null)
                .cityId(entity.getCity() != null ? entity.getCity().getId() : null)
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private String blankToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
