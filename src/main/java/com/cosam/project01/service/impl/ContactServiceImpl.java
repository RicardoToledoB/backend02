package com.cosam.project01.service.impl;

import com.cosam.project01.dto.ContactDTO;
import com.cosam.project01.dto.PostulantDTO;
import com.cosam.project01.entity.ContactEntity;
import com.cosam.project01.entity.PostulantEntity;
import com.cosam.project01.repository.ContactRepository;
import com.cosam.project01.repository.PostulantRepository;
import com.cosam.project01.service.IContactService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements IContactService {

    @Autowired
    private ContactRepository repository;

    @Autowired
    private PostulantRepository postulantRepository;

    private ContactDTO mapToDTO(ContactEntity entity) {
        Integer postulantId = entity.getPostulant() != null ? entity.getPostulant().getId() : null;

        PostulantDTO postulantDTO = null;
        if (entity.getPostulant() != null) {
            PostulantEntity p = entity.getPostulant();
            postulantDTO = PostulantDTO.builder()
                    .id(p.getId())
                    .firstName(p.getFirstName())
                    .lastName(p.getLastName())
                    .firstLastName(p.getFirstLastName())
                    .secondLastName(p.getSecondLastName())
                    .rut(p.getRut())
                    .birthdate(p.getBirthdate())
                    .email(p.getEmail())
                    .phone(p.getPhone())
                    .address(p.getAddress())
                    .createdAt(p.getCreatedAt())
                    .updatedAt(p.getUpdatedAt())
                    .deletedAt(p.getDeletedAt())
                    .build();
        }

        return ContactDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .cellphone(entity.getCellphone())
                .email(entity.getEmail())
                .postulantId(postulantId)
                .postulant(postulantDTO)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private Integer resolvePostulantId(ContactDTO dto) {
        if (dto == null) {
            return null;
        }
        if (dto.getPostulantId() != null) {
            return dto.getPostulantId();
        }
        if (dto.getPostulant() != null && dto.getPostulant().getId() != null) {
            return dto.getPostulant().getId();
        }
        return null;
    }

    private PostulantEntity resolvePostulant(ContactDTO dto) {
        Integer postulantId = resolvePostulantId(dto);
        if (postulantId == null) {
            return null;
        }
        return postulantRepository.findById(postulantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulante no encontrado: " + postulantId));
    }

    private ContactEntity mapToEntity(ContactDTO dto) {
        return ContactEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .cellphone(dto.getCellphone())
                .email(dto.getEmail())
                .postulant(resolvePostulant(dto))
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    @Transactional
    public ContactDTO create(ContactDTO dto) {
        ContactEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    @Transactional
    public ContactDTO update(Integer id, ContactDTO dto) {
        ContactEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contacto no encontrado"));

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCellphone(dto.getCellphone());
        entity.setEmail(dto.getEmail());

        Integer postulantId = resolvePostulantId(dto);
        if (postulantId != null) {
            entity.setPostulant(resolvePostulant(dto));
        }

        return mapToDTO(repository.save(entity));
    }

    @Override
    public ContactDTO getById(Integer id) {
        ContactEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contacto no encontrado"));
        return mapToDTO(entity);
    }

    @Override
    public ContactDTO getByPostulantId(Integer postulantId) {
        ContactEntity entity = repository.findFirstByPostulant_IdAndDeletedAtIsNullOrderByIdDesc(postulantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe referente activo para el postulante " + postulantId));
        return mapToDTO(entity);
    }

    @Override
    public List<ContactDTO> getAllByPostulantId(Integer postulantId) {
        return repository.findByPostulant_IdAndDeletedAtIsNullOrderByIdDesc(postulantId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContactDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<ContactDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<ContactDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }

    public List<ContactDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ContactDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ContactDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void restore(Integer id) {
        int updated = repository.restoreById(id);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contacto no encontrado");
        }
    }
}
