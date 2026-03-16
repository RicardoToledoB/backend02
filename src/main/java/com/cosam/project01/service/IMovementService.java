package com.cosam.project01.service;

import com.cosam.project01.dto.MovementDTO;

import java.util.List;

public interface IMovementService {

    MovementDTO create(MovementDTO dto);
    MovementDTO update(Integer id, MovementDTO dto);
    MovementDTO getById(Integer id);
    List<MovementDTO> getAll();
    void delete(Integer id);
}
