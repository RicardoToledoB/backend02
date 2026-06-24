package com.cosam.project01.service.impl;

import com.cosam.project01.demand.repository.*;
import com.cosam.project01.dto.ProgramDTO;
import com.cosam.project01.entity.ProgramEntity;
import com.cosam.project01.repository.ProgramRepository;
import com.cosam.project01.service.IProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgramServiceImpl implements IProgramService {

    @Autowired
    private ProgramRepository repository;
    @Autowired
    private ProgramPopulationRepository populationRepository;
    @Autowired
    private ProgramModalityRepository modalityRepository;
    @Autowired
    private ProgramPlanRepository planRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private CityRepository cityRepository;

    private ProgramDTO mapToDTO(ProgramEntity entity) {
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

    private ProgramEntity mapToEntity(ProgramDTO dto) {
        ProgramEntity entity = ProgramEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .description(dto.getDescription())
                .active(dto.getActive())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
        applyCatalogs(entity, dto);
        return entity;
    }

    private void applyCatalogs(ProgramEntity entity, ProgramDTO dto) {
        entity.setPopulationType(dto.getPopulationTypeId() != null ? populationRepository.findById(dto.getPopulationTypeId()).orElse(null) : null);
        entity.setModality(dto.getModalityId() != null ? modalityRepository.findById(dto.getModalityId()).orElse(null) : null);
        entity.setPlan(dto.getPlanId() != null ? planRepository.findById(dto.getPlanId()).orElse(null) : null);
        entity.setRegion(dto.getRegionId() != null ? regionRepository.findById(dto.getRegionId()).orElse(null) : null);
        entity.setCity(dto.getCityId() != null ? cityRepository.findById(dto.getCityId()).orElse(null) : null);
    }

    public ProgramDTO create(ProgramDTO dto) {
        ProgramEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public ProgramDTO update(Integer id, ProgramDTO dto) {
        ProgramEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found"));
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive());
        applyCatalogs(entity, dto);
        return mapToDTO(repository.save(entity));
    }

    @Override
    public ProgramDTO getById(Integer id) {
        ProgramEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<ProgramDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<ProgramDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<ProgramDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }

    public List<ProgramDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProgramDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProgramDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        ProgramEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Program not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }
}
