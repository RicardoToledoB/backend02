package com.cosam.project01.security.dto;

public record ProfileDTO(
        Integer id,
        String email,
        String username,
        String fullName
) {}