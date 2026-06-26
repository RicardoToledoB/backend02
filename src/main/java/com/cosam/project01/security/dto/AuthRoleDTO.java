package com.cosam.project01.security.dto;

public record AuthRoleDTO(
        Integer id,
        String name,
        String code,
        String description,
        Boolean active,
        Integer assignedByUserId
) {}
