package com.cosam.project01.service.impl;
import com.cosam.project01.dto.*;
import com.cosam.project01.entity.*;
import com.cosam.project01.repository.*;
import com.cosam.project01.service.IRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RegisterServiceImpl implements IRegisterService {

    @Autowired
    private RegisterRepository repository;

    @Autowired private PostulantRepository postulantRepository;
    @Autowired private ContactTypeRepository contactTypeRepository;
    @Autowired private SenderRepository senderRepository;
    @Autowired private DiverterRepository diverterRepository;
    @Autowired private ProgramRepository programRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private StateRepository stateRepository;
    @Autowired private ResultRepository resultRepository;
    @Autowired private ContactRepository contactRepository;


    private RegisterDTO mapToDTO(RegisterEntity entity) {
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




    private RegisterEntity mapToEntity(RegisterDTO dto) {
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
                .state(mapToStateEntity(dto.getState()))
                .number_tto(dto.getNumber_tto())
                .is_history(dto.getIs_history())
                .result(mapToResultEntity(dto.getResult()))
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
                .contacts(entity.getContacts() != null
                        ? entity.getContacts().stream().map(this::mapToContactDTO).collect(Collectors.toList())
                        : null)
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
                .postulant(
                        entity.getPostulant() != null
                                ? mapToPostulantDTO(entity.getPostulant())
                                : null
                )
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    /* =======================
       MAP DTO -> ENTITY (CREATE)
       ======================= */
    private ContactEntity mapToContactEntity(ContactDTO dto) {

        if (dto.getPostulant() == null || dto.getPostulant().getId() == null) {
            throw new IllegalArgumentException("postulant.id es obligatorio");
        }

        return ContactEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .cellphone(dto.getCellphone())
                .description(dto.getDescription())
                // 👉 SOLO referencia por ID
                .postulant(
                        PostulantEntity.builder()
                                .id(dto.getPostulant().getId())
                                .build()
                )
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
    

    public RegisterDTO create(RegisterDTO dto) {
        RegisterEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public RegisterDTO update(Integer id, RegisterDTO dto) {

        RegisterEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Register not found"));

        // Campos simples
        entity.setDate_attention(dto.getDate_attention());
        entity.setDescription(dto.getDescription());
        entity.setNumber_tto(dto.getNumber_tto());
        entity.setIs_history(dto.getIs_history());

        // Relaciones (solo si vienen)
        if (dto.getPostulant() != null && dto.getPostulant().getId() != null) {
            entity.setPostulant(
                    postulantRepository.getReferenceById(dto.getPostulant().getId())
            );
        }

        if (dto.getContactType() != null && dto.getContactType().getId() != null) {
            entity.setContactType(
                    contactTypeRepository.getReferenceById(dto.getContactType().getId())
            );
        }

        if (dto.getSender() != null && dto.getSender().getId() != null) {
            entity.setSender(
                    senderRepository.getReferenceById(dto.getSender().getId())
            );
        }

        if (dto.getDiverter() != null && dto.getDiverter().getId() != null) {
            entity.setDiverter(
                    diverterRepository.getReferenceById(dto.getDiverter().getId())
            );
        }

        if (dto.getProgram() != null && dto.getProgram().getId() != null) {
            entity.setProgram(
                    programRepository.getReferenceById(dto.getProgram().getId())
            );
        }

        if (dto.getUser() != null && dto.getUser().getId() != null) {
            entity.setUser(
                    userRepository.getReferenceById(dto.getUser().getId())
            );
        }

        if (dto.getState() != null && dto.getState().getId() != null) {
            entity.setState(
                    stateRepository.getReferenceById(dto.getState().getId())
            );
        }

        if (dto.getResult() != null && dto.getResult().getId() != null) {
            entity.setResult(
                    resultRepository.getReferenceById(dto.getResult().getId())
            );
        }

        if (dto.getContact() != null && dto.getContact().getId() != null) {
            entity.setContact(
                    contactRepository.getReferenceById(dto.getContact().getId())
            );
        }

        RegisterEntity saved = repository.save(entity);
        return mapToDTO(saved);
    }


    @Override
    public RegisterDTO getById(Integer id) {
        RegisterEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<RegisterDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }


    public Page<RegisterDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<RegisterDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<RegisterDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<RegisterDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<RegisterDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        RegisterEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    public Page<RegisterDTO> searchByRut(String rut, Pageable pageable) {
        return repository.searchByRut(rut, pageable)
                .map(this::mapToDTO);
    }
}
