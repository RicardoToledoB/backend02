package com.cosam.project01.service;

import com.cosam.project01.dto.PostulantDTO;

import java.util.List;

public interface IPostulantService {

    PostulantDTO create(PostulantDTO dto);

    PostulantDTO update(Integer id, PostulantDTO dto);

    PostulantDTO getById(Integer id);

    List<PostulantDTO> getAll();

    void delete(Integer id);
}
