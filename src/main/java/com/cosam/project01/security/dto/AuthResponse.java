package com.cosam.project01.security.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record AuthResponse(
        Boolean authenticated,
        String result,
        String message,
        String tokenType,
        String token,
        Long expiresInMs,
        Instant expiresAt,
        AuthUserDTO user,
        List<AuthRoleDTO> roles,
        List<AuthProgramDTO> programs,
        List<String> authorities,
        Map<String, Object> claims
) {}
