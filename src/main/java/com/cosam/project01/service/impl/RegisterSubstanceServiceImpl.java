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

    /*
   private SubstanceDTO substance;
   private RegisterDTO register;
    */

    @Autowired
    private RegisterSubstanceRepository repository;


    private RegisterSubstanceDTO mapToDTO(RegisterSubstanceEntity entity) {
        return RegisterSubstanceDTO.builder()
                .id(entity.getId())
                .register(mapToRegisterDTO(entity.getRegister()))
                .substance(mapToSubstanceDTO(entity.getSubstance()))
                .createdAt(entity.getCreatedAt())
                .level(entity.getLevel())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private RegisterSubstanceEntity mapToEntity(RegisterSubstanceDTO dto) {
        return RegisterSubstanceEntity.builder()
                .id(dto.getId())
                .register(mapToRegisterEntity(dto.getRegister()))
                .substance(mapToSubstanceEntity(dto.getSubstance()))
                .level(dto.getLevel())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    private RegisterDTO mapToRegisterDTO(RegisterEntity entity) {
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
        return RegisterEntity.builder()
                .id(dto.getId())
                .postulant(mapToPostulantEntity(dto.getPostulant()))
                .contactType(mapToContactTypeEntity(dto.getContactType()))
                .sender(mapToSenderEntity(dto.getSender()))
                .diverter(mapToDiverterEntity(dto.getDiverter()))
                .program(mapToProgramEntity(dto.getProgram()))
                .user(mapToUserEntity(dto.getUser()))
                .date_attention(dto.getDate_attention())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    /*FIN MAPEO PRINCIPAL*/

    private PostulantDTO mapToPostulantDTO(PostulantEntity entity) {


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

    private UserEntity mapToUserEntity(UserDTO dto) {
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


    private ContactTypeDTO mapToContactTypeDTO(ContactTypeEntity entity) {
        return ContactTypeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private ContactTypeEntity mapToContactTypeEntity(ContactTypeDTO dto) {
        return ContactTypeEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    private SenderDTO mapToSenderDTO(SenderEntity entity) {
        return SenderDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private SenderEntity mapToSenderEntity(SenderDTO dto) {
        return SenderEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private DiverterDTO mapToDiverterDTO(DiverterEntity entity) {
        return DiverterDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private DiverterEntity mapToDiverterEntity(DiverterDTO dto) {
        return DiverterEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    private SubstanceDTO mapToSubstanceDTO(SubstanceEntity entity) {
        return SubstanceDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    private SubstanceEntity mapToSubstanceEntity(SubstanceDTO dto) {
        return SubstanceEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }




    public RegisterSubstanceDTO create(RegisterSubstanceDTO dto) {
        RegisterSubstanceEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public RegisterSubstanceDTO update(Integer id, RegisterSubstanceDTO dto) {
        RegisterSubstanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));


                entity.setRegister(mapToRegisterEntity(dto.getRegister()));
                entity.setSubstance(mapToSubstanceEntity(dto.getSubstance()));
                entity.setLevel(dto.getLevel());

        return mapToDTO(repository.save(entity));
    }

    @Override
    public RegisterSubstanceDTO getById(Integer id) {
        RegisterSubstanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
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
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<RegisterSubstanceDTO> getAllPaginated(Integer id, Pageable pageable) {
        return repository.search(id, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
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
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    public Page<RegisterSubstanceDTO> searchByRegisterId(Integer registerId, Pageable pageable) {
        return repository.searchByRegisterId(registerId, pageable)
                .map(this::mapToDTO);
    }

    @Transactional
    public int deleteByRegisterId(Integer registerId) {
        return repository.softDeleteByRegisterId(registerId);
    }

}
