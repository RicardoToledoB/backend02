package com.cosam.project01.security.dto;

public record AuthProgramDTO(
        Integer id,
        String name,
        Integer populationTypeId,
        String populationTypeName,
        Integer modalityId,
        String modalityName,
        Integer planId,
        String planName,
        Integer regionId,
        String regionName,
        Integer cityId,
        String cityName,
        String address,
        String phone,
        String email,
        String description,
        Boolean active,
        Boolean isActive,
        Boolean isSupervisor,
        Boolean canReceiveReferences,
        Boolean canManageDemands,
        Boolean canViewDashboard,
        String roleInProgram
) {}
