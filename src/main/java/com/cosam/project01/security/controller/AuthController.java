package com.cosam.project01.security.controller;

import com.cosam.project01.entity.ProgramEntity;
import com.cosam.project01.entity.RoleEntity;
import com.cosam.project01.entity.UserEntity;
import com.cosam.project01.entity.UserProgramEntity;
import com.cosam.project01.entity.UserRoleEntity;
import com.cosam.project01.repository.UserProgramRepository;
import com.cosam.project01.repository.UserRepository;
import com.cosam.project01.repository.UserRoleRepository;
import com.cosam.project01.security.JwtService;
import com.cosam.project01.security.dto.AuthProgramDTO;
import com.cosam.project01.security.dto.AuthRequest;
import com.cosam.project01.security.dto.AuthResponse;
import com.cosam.project01.security.dto.AuthRoleDTO;
import com.cosam.project01.security.dto.AuthUserDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

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
    @Transactional
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        Authentication auth = new UsernamePasswordAuthenticationToken(req.email(), req.password());
        authenticationManager.authenticate(auth);

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.email());
        UserEntity user = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));

        AuthSession session = buildSession(user);
        String token = jwtService.generateToken(userDetails, session.claims());
        Instant expiresAt = Instant.now().plusMillis(jwtService.getExpirationMs());

        return ResponseEntity.ok(new AuthResponse(
                true,
                "OK",
                "Login correcto",
                "Bearer",
                token,
                jwtService.getExpirationMs(),
                expiresAt,
                session.user(),
                session.roles(),
                session.programs(),
                session.authorities(),
                session.claims()
        ));
    }

    @GetMapping("/me")
    @Transactional
    public ResponseEntity<AuthResponse> me(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
        AuthSession session = buildSession(user);

        return ResponseEntity.ok(new AuthResponse(
                true,
                "OK",
                "Usuario autenticado",
                "Bearer",
                null,
                null,
                null,
                session.user(),
                session.roles(),
                session.programs(),
                session.authorities(),
                session.claims()
        ));
    }

    private AuthSession buildSession(UserEntity user) {
        List<UserRoleEntity> userRoles = userRoleRepository.findActiveRolesWithRoleByUserId(user.getId());
        List<UserProgramEntity> userPrograms = userProgramRepository.findActiveProgramsWithProgramByUserId(user.getId());

        AuthUserDTO userDTO = toUserDTO(user);
        List<AuthRoleDTO> roles = userRoles.stream().map(this::toRoleDTO).toList();
        List<AuthProgramDTO> programs = userPrograms.stream().map(this::toProgramDTO).toList();

        List<String> authorities = roles.stream()
                .map(role -> normalizeAuthority(firstNonBlank(role.code(), role.name())))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("userId", userDTO.id());
        claims.put("email", userDTO.email());
        claims.put("username", userDTO.username());
        claims.put("rut", userDTO.rut());
        claims.put("fullName", userDTO.fullName());
        claims.put("roles", roles.stream().map(role -> {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("id", role.id());
            r.put("name", role.name());
            r.put("code", firstNonBlank(role.code(), role.name()));
            return r;
        }).toList());
        claims.put("authorities", authorities);
        claims.put("programIds", programs.stream().map(AuthProgramDTO::id).filter(Objects::nonNull).toList());
        claims.put("programs", programs.stream().map(program -> {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("id", program.id());
            p.put("name", program.name());
            p.put("isSupervisor", Boolean.TRUE.equals(program.isSupervisor()));
            p.put("canReceiveReferences", Boolean.TRUE.equals(program.canReceiveReferences()));
            p.put("canManageDemands", Boolean.TRUE.equals(program.canManageDemands()));
            p.put("canViewDashboard", Boolean.TRUE.equals(program.canViewDashboard()));
            p.put("roleInProgram", program.roleInProgram());
            return p;
        }).toList());

        return new AuthSession(userDTO, roles, programs, authorities, claims);
    }

    private AuthUserDTO toUserDTO(UserEntity user) {
        String fullName = Stream.of(
                        user.getFirstName(),
                        user.getSecondName(),
                        user.getFirstLastName(),
                        user.getSecondLastName()
                )
                .filter(value -> value != null && !value.isBlank())
                .reduce((a, b) -> a + " " + b)
                .orElse(user.getUsername());

        return new AuthUserDTO(
                user.getId(),
                user.getFirstName(),
                user.getSecondName(),
                user.getFirstLastName(),
                user.getSecondLastName(),
                fullName,
                user.getEmail(),
                user.getUsername(),
                user.getRut()
        );
    }

    private AuthRoleDTO toRoleDTO(UserRoleEntity userRole) {
        RoleEntity role = userRole.getRole();
        return new AuthRoleDTO(
                role != null ? role.getId() : null,
                role != null ? role.getName() : null,
                role != null ? role.getCode() : null,
                role != null ? role.getDescription() : null,
                role != null ? role.getActive() : null,
                userRole.getAssignedByUser() != null ? userRole.getAssignedByUser().getId() : null
        );
    }

    private AuthProgramDTO toProgramDTO(UserProgramEntity userProgram) {
        ProgramEntity program = userProgram.getProgram();
        return new AuthProgramDTO(
                program != null ? program.getId() : null,
                program != null ? program.getName() : null,
                program != null && program.getPopulationType() != null ? program.getPopulationType().getId() : null,
                program != null && program.getPopulationType() != null ? program.getPopulationType().getName() : null,
                program != null && program.getModality() != null ? program.getModality().getId() : null,
                program != null && program.getModality() != null ? program.getModality().getName() : null,
                program != null && program.getPlan() != null ? program.getPlan().getId() : null,
                program != null && program.getPlan() != null ? program.getPlan().getName() : null,
                program != null && program.getRegion() != null ? program.getRegion().getId() : null,
                program != null && program.getRegion() != null ? program.getRegion().getName() : null,
                program != null && program.getCity() != null ? program.getCity().getId() : null,
                program != null && program.getCity() != null ? program.getCity().getName() : null,
                program != null ? program.getAddress() : null,
                program != null ? program.getPhone() : null,
                program != null ? program.getEmail() : null,
                program != null ? program.getDescription() : null,
                program != null ? program.getActive() : null,
                userProgram.getIsActive(),
                userProgram.getIsSupervisor(),
                userProgram.getCanReceiveReferences(),
                userProgram.getCanManageDemands(),
                userProgram.getCanViewDashboard(),
                userProgram.getRoleInProgram()
        );
    }

    private String normalizeAuthority(String value) {
        if (value == null || value.isBlank()) return null;
        String normalized = value.trim().toUpperCase().replace(' ', '_');
        return normalized.startsWith("ROLE_") ? normalized : "ROLE_" + normalized;
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) return first;
        if (second != null && !second.isBlank()) return second;
        return null;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private record AuthSession(
            AuthUserDTO user,
            List<AuthRoleDTO> roles,
            List<AuthProgramDTO> programs,
            List<String> authorities,
            Map<String, Object> claims
    ) {}
}
