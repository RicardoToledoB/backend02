package com.cosam.project01.service;

import com.cosam.project01.dto.RegisterMovementDTO;

import java.util.List;

public interface IRegisterMovementService {

    RegisterMovementDTO create(RegisterMovementDTO dto);
    RegisterMovementDTO update(Integer id, RegisterMovementDTO dto);
    RegisterMovementDTO getById(Integer id);
    List<RegisterMovementDTO> getAll();
    void delete(Integer id);
}
