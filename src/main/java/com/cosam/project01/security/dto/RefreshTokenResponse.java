package com.cosam.project01.security.dto;

import java.time.Instant;

public record RefreshTokenResponse(
        String tokenType,
        String token,
        String refreshToken,
        Long expiresInMs,
        Instant expiresAt
) {}
