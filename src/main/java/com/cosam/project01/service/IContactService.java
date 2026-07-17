package com.cosam.project01.service;

import com.cosam.project01.dto.ContactDTO;

import java.util.List;

public interface IContactService {
    ContactDTO create(ContactDTO dto);

    ContactDTO update(Integer id, ContactDTO dto);

    ContactDTO getById(Integer id);

    ContactDTO getByPostulantId(Integer postulantId);

    List<ContactDTO> getAllByPostulantId(Integer postulantId);

    List<ContactDTO> getAll();

    void delete(Integer id);
}
