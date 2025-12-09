package com.cosam.project01.service.impl;

import com.cosam.project01.dto.*;
import com.cosam.project01.entity.*;
import com.cosam.project01.repository.PostulantRepository;
import com.cosam.project01.service.IPostulantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostulantServiceImpl implements IPostulantService {

    @Autowired
    private PostulantRepository repository;


    private PostulantDTO mapToDTO(PostulantEntity entity) {


        return PostulantDTO.builder()
                .id(entity.getId())
                .user(mapToUserDTO(entity.getUser()))
                .commune(mapToCommuneDTO(entity.getCommune()))
                .address(entity.getAddress())
                .sex(mapToSexDTO(entity.getSex()))
                .convPrev(mapToConvPrevDTO(entity.getConvPrev()))
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .rut(entity.getRut())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .secondLastName(entity.getSecondLastName())
                .firstLastName(entity.getFirstLastName())
                .notRelevant(mapToNotRelevantDTO(entity.getNotRelevant()))
                .birthdate(entity.getBirthdate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .contacts(entity.getContacts() != null
                        ? entity.getContacts().stream().map(this::mapToContactDTO).collect(Collectors.toList())
                        : null)
                .build();
    }

    private ContactDTO mapToContactDTO(ContactEntity entity) {
        return ContactDTO.builder()
                .id(entity.getId())
                .cellphone(entity.getCellphone())
                .name(entity.getName())
                .email(entity.getEmail())
                .description(entity.getDescription())
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
                .convPrev(mapToConvPrevEntity(dto.getConvPrev()))
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .rut(dto.getRut())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .secondLastName(dto.getSecondLastName())
                .firstLastName(dto.getFirstLastName())
                .birthdate(dto.getBirthdate())
                .notRelevant(mapToRelevantEntity(dto.getNotRelevant()))
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private NotRelevantDTO mapToNotRelevantDTO(NotRelevantEntity entity) {
        return NotRelevantDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private NotRelevantEntity mapToRelevantEntity(NotRelevantDTO dto) {
        return NotRelevantEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private SexEntity mapToSexEntity(SexDTO dto) {
        return SexEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    private SexDTO mapToSexDTO(SexEntity entity) {


        return SexDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }





    private CommuneDTO mapToCommuneDTO(CommuneEntity entity) {


        return CommuneDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private CommuneEntity mapToCommuneEntity(CommuneDTO dto) {
        return CommuneEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
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
                .secondLastName(entity.getSecondLastName())
                .firstLastName(entity.getFirstLastName())
                .secondLastName(entity.getSecondLastName())
                .username(entity.getUsername())
                .rut(entity.getRut())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }





    private UserEntity mapToUserEntity(UserDTO dto) {
        return UserEntity.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .secondLastName(dto.getSecondLastName())
                .secondLastName(dto.getSecondLastName())
                .firstLastName(dto.getFirstLastName())
                .secondLastName(dto.getSecondLastName())
                .username(dto.getUsername())
                .rut(dto.getRut())
                .email(dto.getEmail())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private ProgramDTO mapToProgramDTO(ProgramEntity entity) {


        return ProgramDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private ProgramEntity mapToProgramEntity(ProgramDTO dto) {
        return ProgramEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    private ConvPrevDTO mapToConvPrevDTO(ConvPrevEntity entity) {
        return ConvPrevDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .intPrev(mapToIntPrevDTO(entity.getIntPrev()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private ConvPrevEntity mapToConvPrevEntity(ConvPrevDTO dto) {
        return ConvPrevEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .intPrev(mapToIntPrevEntity(dto.getIntPrev()))
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private IntPrevDTO mapToIntPrevDTO(IntPrevEntity entity) {
        return IntPrevDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private IntPrevEntity mapToIntPrevEntity(IntPrevDTO dto) {
        return IntPrevEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }





    public PostulantDTO create(PostulantDTO dto) {
        PostulantEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public PostulantDTO update(Integer id, PostulantDTO dto) {
        PostulantEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

                entity.setAddress(dto.getAddress());
                entity.setEmail(dto.getEmail());
                entity.setPhone(dto.getPhone());
                entity.setRut(dto.getRut());
                entity.setFirstName(dto.getFirstName());
                entity.setLastName(dto.getLastName());
                entity.setBirthdate(dto.getBirthdate());
                entity.setUser(mapToUserEntity(dto.getUser()));
                entity.setCommune(mapToCommuneEntity(dto.getCommune()));
                entity.setConvPrev(mapToConvPrevEntity(dto.getConvPrev()));
                entity.setFirstLastName(dto.getFirstLastName());
                entity.setSecondLastName(dto.getSecondLastName());
                entity.setSex(mapToSexEntity(dto.getSex()));
                entity.setNotRelevant(mapToRelevantEntity(dto.getNotRelevant()));

        return mapToDTO(repository.save(entity));
    }



    @Override
    public PostulantDTO getById(Integer id) {
        PostulantEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
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
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    public Page<PostulantDTO> searchByRut(String rut, Pageable pageable) {
        return repository.searchByRut(rut, pageable)
                .map(this::mapToDTO);
    }

}
