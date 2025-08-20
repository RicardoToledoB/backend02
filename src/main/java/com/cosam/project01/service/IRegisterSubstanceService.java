package com.cosam.project01.service;

import com.cosam.project01.dto.RegisterSubstanceDTO;

import java.util.List;

public interface IRegisterSubstanceService {

    RegisterSubstanceDTO create(RegisterSubstanceDTO dto);

    RegisterSubstanceDTO update(Integer id, RegisterSubstanceDTO dto);

    RegisterSubstanceDTO getById(Integer id);

    List<RegisterSubstanceDTO> getAll();

    void delete(Integer id);
}
