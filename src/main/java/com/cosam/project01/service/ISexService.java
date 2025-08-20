package com.cosam.project01.service;

import com.cosam.project01.dto.SexDTO;

import java.util.List;

public interface ISexService {

    SexDTO create(SexDTO dto);

    SexDTO update(Integer id, SexDTO dto);

    SexDTO getById(Integer id);

    List<SexDTO> getAll();

    void delete(Integer id);
}
