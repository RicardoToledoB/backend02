package com.cosam.project01.service;

import com.cosam.project01.dto.UserProgramDTO;

import java.util.List;

public interface IUserProgramService {

    UserProgramDTO create(UserProgramDTO dto);

    UserProgramDTO update(Integer id, UserProgramDTO dto);

    UserProgramDTO getById(Integer id);

    List<UserProgramDTO> getAll();

    void delete(Integer id);
}
