package com.cosam.project01.service.impl;

import com.cosam.project01.dto.ContactTypeDTO;
import com.cosam.project01.dto.ContactTypeDTO;
import com.cosam.project01.entity.ContactTypeEntity;
import com.cosam.project01.entity.ContactTypeEntity;
import com.cosam.project01.repository.ContactTypeRepository;
import com.cosam.project01.service.IContactTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactTypeServiceImpl implements IContactTypeService {

    @Autowired
    private ContactTypeRepository repository;


    private ContactTypeDTO mapToDTO(ContactTypeEntity entity) {
        return ContactTypeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private ContactTypeEntity mapToEntity(ContactTypeDTO dto) {
        return ContactTypeEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    public ContactTypeDTO create(ContactTypeDTO dto) {
        ContactTypeEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public ContactTypeDTO update(Integer id, ContactTypeDTO dto) {
        ContactTypeEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setName(dto.getName());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public ContactTypeDTO getById(Integer id) {
        ContactTypeEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<ContactTypeDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<ContactTypeDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<ContactTypeDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<ContactTypeDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<ContactTypeDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<ContactTypeDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        ContactTypeEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }
    
}
