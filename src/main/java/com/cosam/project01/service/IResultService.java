package com.cosam.project01.service;

import com.cosam.project01.dto.ResultDTO;

import java.util.List;

public interface IResultService {

    ResultDTO create(ResultDTO dto);
    ResultDTO update(Integer id, ResultDTO dto);
    ResultDTO getById(Integer id);
    List<ResultDTO> getAll();
    void delete(Integer id);
}
