package com.cosam.project01.service;

import com.cosam.project01.dto.ProfessionDTO;

import java.util.List;

public interface IProfessionService {

    ProfessionDTO create(ProfessionDTO dto);
    ProfessionDTO update(Integer id, ProfessionDTO dto);
    ProfessionDTO getById(Integer id);
    List<ProfessionDTO> getAll();
    void delete(Integer id);
}
