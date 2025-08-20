package com.cosam.project01.service;

import com.cosam.project01.dto.RegisterDTO;

import java.util.List;

public interface IRegisterService {

    RegisterDTO create(RegisterDTO dto);

    RegisterDTO update(Integer id, RegisterDTO dto);

    RegisterDTO getById(Integer id);

    List<RegisterDTO> getAll();

    void delete(Integer id);
}
