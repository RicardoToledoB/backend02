package com.cosam.project01.service.impl;

import com.cosam.project01.dto.*;
import com.cosam.project01.entity.*;
import com.cosam.project01.repository.RegisterMovementRepository;
import com.cosam.project01.service.IRegisterMovementService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegisterMovementServiceImpl implements IRegisterMovementService {

    @Autowired
    private RegisterMovementRepository repository;


    private RegisterMovementDTO mapToDTO(RegisterMovementEntity entity) {
        return RegisterMovementDTO.builder()
                .id(entity.getId())
                .register(mapToRegisterDTO(entity.getRegister()))
                .profession(mapToProfessionDTO(entity.getProfession()))
                .full_name(entity.getFull_name())
                .date_attention(entity.getDate_attention())
                .hour_attention(entity.getHour_attention())
                .state(entity.getState())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private RegisterMovementEntity mapToEntity(RegisterMovementDTO dto) {
        return RegisterMovementEntity.builder()
                .id(dto.getId())
                .register(mapToRegisterEntity(dto.getRegister()))
                .profession(mapToProfessionEntity(dto.getProfession()))
                .full_name(dto.getFull_name())
                .date_attention(dto.getDate_attention())
                .hour_attention(dto.getHour_attention())
                .state(dto.getState())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private RegisterDTO mapToRegisterDTO(RegisterEntity entity) {
        return RegisterDTO.builder()
                .id(entity.getId())
                .postulant(entity.getPostulant() != null ? mapToPostulantDTO(entity.getPostulant()) : null)
                .contactType(entity.getContactType() != null ? mapToContactTypeDTO(entity.getContactType()) : null)
                .sender(entity.getSender() != null ? mapToSenderDTO(entity.getSender()) : null)
                .diverter(entity.getDiverter() != null ? mapToDiverterDTO(entity.getDiverter()) : null)
                .program(entity.getProgram() != null ? mapToProgramDTO(entity.getProgram()) : null)
                .user(entity.getUser() != null ? mapToUserDTO(entity.getUser()) : null)
                .state(entity.getState() != null ? mapToStateDTO(entity.getState()) : null)
                .result(entity.getResult() != null ? mapToResultDTO(entity.getResult()) : null)
                .date_attention(entity.getDate_attention())
                .contact(entity.getContact() != null ? mapToContactDTO(entity.getContact()) : null)
                .description(entity.getDescription())
                .number_tto(entity.getNumber_tto())
                .is_history(entity.getIs_history())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }




    private RegisterEntity mapToRegisterEntity(RegisterDTO dto) {
        return RegisterEntity.builder()
                .id(dto.getId())
                .postulant(dto.getPostulant() != null ? mapToPostulantEntity(dto.getPostulant()) : null)
                .contactType(dto.getContactType() != null ? mapToContactTypeEntity(dto.getContactType()) : null)
                .sender(dto.getSender() != null ? mapToSenderEntity(dto.getSender()) : null)
                .diverter(dto.getDiverter() != null ? mapToDiverterEntity(dto.getDiverter()) : null)
                .program(dto.getProgram() != null ? mapToProgramEntity(dto.getProgram()) : null)
                .user(dto.getUser() != null ? mapToUserEntity(dto.getUser()) : null)
                .state(dto.getState() != null ? mapToStateEntity(dto.getState()) : null)
                .result(dto.getResult() != null ? mapToResultEntity(dto.getResult()) : null)
                .date_attention(dto.getDate_attention())
                .contact(dto.getContact() != null ? mapToContactEntity(dto.getContact()) : null)
                .description(dto.getDescription())
                .number_tto(dto.getNumber_tto())
                .is_history(dto.getIs_history())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    /*FIN MAPEO PRINCIPAL*/

    private StateDTO mapToStateDTO(StateEntity entity) {
        return StateDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private StateEntity mapToStateEntity(StateDTO dto) {
        return StateEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }



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
                .convPrev(mapToConvPrevDTO(entity.getConvPrev()))
                .secondLastName(entity.getSecondLastName())
                .firstLastName(entity.getFirstLastName())
                .birthdate(entity.getBirthdate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())

                .build();
    }

    private ContactDTO mapToContactDTO(ContactEntity entity) {
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
    private ContactEntity mapToContactEntity(ContactDTO dto) {



        return ContactEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .cellphone(dto.getCellphone())
                .description(dto.getDescription())
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
                .convPrev(mapToConvPrevEntity(dto.getConvPrev()))
                .secondLastName(dto.getSecondLastName())
                .firstLastName(dto.getFirstLastName())
                .birthdate(dto.getBirthdate())
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




    /*





    private String date_attention;

    private String description;

    */

    private ResultDTO mapToResultDTO(ResultEntity entity) {
        return ResultDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private ResultEntity mapToResultEntity(ResultDTO dto) {
        return ResultEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    private ProfessionDTO mapToProfessionDTO(ProfessionEntity entity) {
        return ProfessionDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private ProfessionEntity mapToProfessionEntity(ProfessionDTO dto) {
        return ProfessionEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    public RegisterMovementDTO create(RegisterMovementDTO dto) {
        RegisterMovementEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public RegisterMovementDTO update(Integer id, RegisterMovementDTO dto) {
        RegisterMovementEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setRegister(mapToRegisterEntity(dto.getRegister()));
        entity.setProfession(mapToProfessionEntity(dto.getProfession()));
        entity.setFull_name(dto.getFull_name());
        entity.setDate_attention(dto.getDate_attention());
        entity.setHour_attention(dto.getHour_attention());
        entity.setState(dto.getState());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public RegisterMovementDTO getById(Integer id) {
        RegisterMovementEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<RegisterMovementDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public Page<RegisterMovementDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<RegisterMovementDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<RegisterMovementDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<RegisterMovementDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<RegisterMovementDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        RegisterMovementEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    public Page<RegisterMovementDTO> searchByRegisterId(Integer registerId, Pageable pageable) {
        return repository.searchByRegisterId(registerId, pageable)
                .map(this::mapToDTO);
    }


    @Transactional
    public int deleteByRegisterId(Integer registerId) {
        return repository.softDeleteByRegisterId(registerId);
    }

}
