package com.cosam.project01.service;

import com.cosam.project01.dto.CommuneDTO;

import java.util.List;

public interface ICommuneService {

    CommuneDTO create(CommuneDTO dto);
    CommuneDTO update(Integer id, CommuneDTO dto);
    CommuneDTO getById(Integer id);
    List<CommuneDTO> getAll();
    void delete(Integer id);
}
