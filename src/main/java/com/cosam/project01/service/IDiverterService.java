package com.cosam.project01.service;

import com.cosam.project01.dto.DiverterDTO;

import java.util.List;

public interface IDiverterService {

    DiverterDTO create(DiverterDTO dto);

    DiverterDTO update(Integer id, DiverterDTO dto);

    DiverterDTO getById(Integer id);

    List<DiverterDTO> getAll();

    void delete(Integer id);
}
