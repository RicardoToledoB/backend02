package com.cosam.project01.service;

import com.cosam.project01.dto.NotRelevantDTO;

import java.util.List;

public interface INotRelevantService {

    NotRelevantDTO create(NotRelevantDTO dto);

    NotRelevantDTO update(Integer id, NotRelevantDTO dto);

    NotRelevantDTO getById(Integer id);

    List<NotRelevantDTO> getAll();

    void delete(Integer id);
}
