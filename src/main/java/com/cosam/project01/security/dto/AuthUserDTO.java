package com.cosam.project01.security.dto;

public record AuthUserDTO(
        Integer id,
        String firstName,
        String secondName,
        String firstLastName,
        String secondLastName,
        String fullName,
        String email,
        String username,
        String rut
) {}
