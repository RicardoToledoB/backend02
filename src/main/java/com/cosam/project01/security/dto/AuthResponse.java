package com.cosam.project01.security.dto;

import com.cosam.project01.dto.ProgramDTO;
import com.cosam.project01.dto.RoleDTO;

import java.util.List;
import java.util.Map;
import java.util.List;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        List<RoleDTO> roles,
        List<ProgramDTO> programs,
        ProfileDTO profile,
        String refreshToken
) {}