package com.cosam.project01.security.dto;

import java.util.List;
import java.util.Map;

public record AuthResponse(
        String token,
        String tokenType,     // "Bearer"
        long   expiresIn,     // en ms
        List<String> roles,
        List<String> programs,
        Map<String, Object> profile,
        String refreshToken   // nuevo campo
) {}
