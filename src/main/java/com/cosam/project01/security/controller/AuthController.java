// com.cosam.project01.security.controller.AuthController
package com.cosam.project01.security.controller;

import com.cosam.project01.entity.UserEntity;
import com.cosam.project01.repository.UserRepository;
import com.cosam.project01.repository.UserRoleRepository;
import com.cosam.project01.repository.UserProgramRepository;
import com.cosam.project01.security.JwtService;
import com.cosam.project01.security.dto.AuthRequest;
import com.cosam.project01.security.dto.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        // autenticar (username=email)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        // cargar principal para firmar el JWT
        UserDetails principal = userDetailsService.loadUserByUsername(req.email());

        // cargar usuario + roles + programs desde DB
        UserEntity u = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado"));

        List<String> roles = userRoleRepository.findRoleNamesByUserId(u.getId()).stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        List<String> programs = userProgramRepository.findProgramNamesByUserId(u.getId());

        // armar fullName
        String fullName = String.join(" ",
                safe(u.getFirstName()),
                safe(u.getSecondName()),
                safe(u.getFirstLastName()),
                safe(u.getSecondLastName())
        ).replaceAll("\\s+", " ").trim();

        // claims extra al JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("programs", programs);
        claims.put("username", u.getUsername());
        claims.put("email", u.getEmail());
        claims.put("fullName", fullName);
        // Si necesitas IDs también:
        // claims.put("programIds", userProgramRepository.findProgramIdsByUserId(u.getId()));

        String token = jwtService.generateToken(principal, claims);

        AuthResponse body = new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                roles,
                programs,
                Map.of(
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "username", u.getUsername(),
                        "fullName", fullName
                )
        );

        return ResponseEntity.ok(body);
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
