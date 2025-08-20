package com.cosam.project01.service;

import com.cosam.project01.dto.ContactTypeDTO;

import java.util.List;

public interface IContactTypeService {

    ContactTypeDTO create(ContactTypeDTO dto);

    ContactTypeDTO update(Integer id, ContactTypeDTO dto);

    ContactTypeDTO getById(Integer id);

    List<ContactTypeDTO> getAll();

    void delete(Integer id);
}
