// com.cosam.project01.security.controller.AuthController
package com.cosam.project01.security.controller;

import com.cosam.project01.dto.ProgramDTO;
import com.cosam.project01.dto.RoleDTO;
import com.cosam.project01.entity.UserEntity;
import com.cosam.project01.repository.UserRepository;
import com.cosam.project01.repository.UserRoleRepository;
import com.cosam.project01.repository.UserProgramRepository;
import com.cosam.project01.security.JwtService;
import com.cosam.project01.security.dto.AuthRequest;
import com.cosam.project01.security.dto.AuthResponse;
import com.cosam.project01.security.dto.ProfileDTO;
import com.cosam.project01.security.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserProgramRepository userProgramRepository;

    private final RefreshTokenService refreshTokenService;


    // =======================================================
    // LOGIN
    // =======================================================
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserDetails principal = userDetailsService.loadUserByUsername(req.email());
        UserEntity u = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado"));

        // ROLES con ID
        List<RoleDTO> roles = userRoleRepository.findRolesByUserIdFull(u.getId())
                .stream()
                .map(r -> new RoleDTO(
                        r.getId(),
                        r.getName(),
                        r.getCreatedAt(),
                        r.getUpdatedAt(),
                        r.getDeletedAt()
                ))
                .toList();

        // PROGRAMAS con ID
        List<ProgramDTO> programs = userProgramRepository.findProgramsByUserIdFull(u.getId())
                .stream()
                .map(p -> new ProgramDTO(
                        p.getId(),
                        p.getName(),
                        p.getCreatedAt(),
                        p.getUpdatedAt(),
                        p.getDeletedAt()
                ))
                .toList();

        // FULLNAME
        String fullName = String.join(" ",
                safe(u.getFirstName()), safe(u.getSecondName()),
                safe(u.getFirstLastName()), safe(u.getSecondLastName())
        ).replaceAll("\\s+", " ").trim();

        // PROFILE con ID
        ProfileDTO profile = new ProfileDTO(
                u.getId(),
                u.getEmail(),
                u.getUsername(),
                fullName
        );

        // CLAIMS → aquí va SOLO TEXTO, no DTO
        Map<String, Object> claims = Map.of(
                "roles", roles.stream().map(RoleDTO::getName).toList(),   // <-- CORREGIDO
                "programs", programs.stream().map(ProgramDTO::getName).toList(), // <-- CORREGIDO
                "username", u.getUsername(),
                "email", u.getEmail(),
                "fullName", fullName
        );

        String token = jwtService.generateToken(principal, claims);
        String refreshToken = refreshTokenService.generateRefreshToken(u.getEmail());

        AuthResponse body = new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                roles,
                programs,
                profile,
                refreshToken
        );

        return ResponseEntity.ok(body);
    }



    // =======================================================
    // REFRESH TOKEN
    // =======================================================
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (refreshToken == null || !refreshTokenService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).body(null);
        }

        String email = refreshTokenService.extractUsername(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(email);
        UserEntity u = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado"));

        // ROLES
        List<RoleDTO> roles = userRoleRepository.findRolesByUserIdFull(u.getId())
                .stream()
                .map(r -> new RoleDTO(
                        r.getId(),
                        r.getName(),
                        r.getCreatedAt(),
                        r.getUpdatedAt(),
                        r.getDeletedAt()
                ))
                .toList();

        // PROGRAMAS
        List<ProgramDTO> programs = userProgramRepository.findProgramsByUserIdFull(u.getId())
                .stream()
                .map(p -> new ProgramDTO(
                        p.getId(),
                        p.getName(),
                        p.getCreatedAt(),
                        p.getUpdatedAt(),
                        p.getDeletedAt()
                ))
                .toList();

        // FULLNAME
        String fullName = String.join(" ",
                safe(u.getFirstName()), safe(u.getSecondName()),
                safe(u.getFirstLastName()), safe(u.getSecondLastName())
        ).replaceAll("\\s+", " ").trim();

        // PROFILE
        ProfileDTO profile = new ProfileDTO(
                u.getId(),
                u.getEmail(),
                u.getUsername(),
                fullName
        );

        // CLAIMS → SOLO NOMBRES
        Map<String, Object> claims = Map.of(
                "roles", roles.stream().map(RoleDTO::getName).toList(), // <-- CORREGIDO
                "programs", programs.stream().map(ProgramDTO::getName).toList(), // <-- CORREGIDO
                "username", u.getUsername(),
                "email", u.getEmail()
        );

        String newAccessToken = jwtService.generateToken(user, claims);
        String newRefreshToken = refreshTokenService.generateRefreshToken(email);

        return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                "Bearer",
                jwtService.getExpirationMs(),
                roles,
                programs,
                profile,
                newRefreshToken
        ));
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
