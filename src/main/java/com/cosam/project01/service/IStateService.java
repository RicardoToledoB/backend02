package com.cosam.project01.service;

import com.cosam.project01.dto.StateDTO;

import java.util.List;

public interface IStateService {
    StateDTO create(StateDTO dto);
    StateDTO update(Integer id, StateDTO dto);
    StateDTO getById(Integer id);
    List<StateDTO> getAll();
    void delete(Integer id);
}
