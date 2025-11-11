package com.cosam.project01.service;

import com.cosam.project01.dto.IntPrevDTO;

import java.util.List;

public interface IIntPrevService {

    IntPrevDTO create(IntPrevDTO dto);
    IntPrevDTO update(Integer id, IntPrevDTO dto);
    IntPrevDTO getById(Integer id);
    List<IntPrevDTO> getAll();
    void delete(Integer id);
}
