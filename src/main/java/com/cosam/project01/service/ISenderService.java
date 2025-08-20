package com.cosam.project01.service;

import com.cosam.project01.dto.SenderDTO;

import java.util.List;

public interface ISenderService {

    SenderDTO create(SenderDTO dto);

    SenderDTO update(Integer id, SenderDTO dto);

    SenderDTO getById(Integer id);

    List<SenderDTO> getAll();

    void delete(Integer id);
}
