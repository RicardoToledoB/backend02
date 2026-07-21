package com.cosam.project01.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RequestTokenValidator {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public UserDetails requireValidAccessToken(HttpServletRequest request) {
        String authHeader = firstNonBlank(
                request.getHeader("Authorization"),
                request.getHeader("X-Forwarded-Authorization"),
                request.getHeader("X-Authorization")
        );

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Bearer requerido");
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Bearer vacío");
        }

        try {
            String email = jwtService.extractUsername(token);
            UserDetails user = userDetailsService.loadUserByUsername(email);
            if (!jwtService.isTokenValid(token, user)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
            }
            return user;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value != null && !value.isBlank()) return value;
        }
        return null;
    }
}
