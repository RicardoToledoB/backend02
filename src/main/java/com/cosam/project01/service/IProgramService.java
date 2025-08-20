package com.cosam.project01.service;

import com.cosam.project01.dto.ProgramDTO;

import java.util.List;

public interface IProgramService {

    ProgramDTO create(ProgramDTO dto);

    ProgramDTO update(Integer id, ProgramDTO dto);

    ProgramDTO getById(Integer id);

    List<ProgramDTO> getAll();

    void delete(Integer id);
}
