package com.cosam.project01.service;

import com.cosam.project01.dto.ConvPrevDTO;

import java.util.List;

public interface IConvPrevService {

    ConvPrevDTO create(ConvPrevDTO dto);
    ConvPrevDTO update(Integer id, ConvPrevDTO dto);
    ConvPrevDTO getById(Integer id);
    List<ConvPrevDTO> getAll();
    void delete(Integer id);
}
