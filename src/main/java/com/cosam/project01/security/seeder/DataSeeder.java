package com.cosam.project01.security.seeder;

import com.cosam.project01.entity.*;
import com.cosam.project01.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public void run(String... args) {
        RoleEntity admin = role("ADMIN");
        RoleEntity administrativo = role("ADMINISTRATIVO");
        role("SUPERVISOR");
        role("PROFESIONAL");

        UserEntity u1 = userRepository.findByEmailIgnoreCase("admin@demo.com")
                .orElseGet(() -> userRepository.save(UserEntity.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("Admin123$"))
                        .email("admin@demo.com")
                        .firstName("Admin")
                        .build()));

        UserEntity u2 = userRepository.findByEmailIgnoreCase("operador@demo.com")
                .orElseGet(() -> userRepository.save(UserEntity.builder()
                        .username("operador")
                        .password(passwordEncoder.encode("Operador123$"))
                        .email("operador@demo.com")
                        .firstName("Operador")
                        .build()));

        assignRole(u1, admin);
        assignRole(u2, administrativo);
    }

    private RoleEntity role(String name) {
        return roleRepository.findByNameIgnoreCase(name)
                .map(existing -> {
                    boolean changed = false;
                    if (existing.getCode() == null || existing.getCode().isBlank()) {
                        existing.setCode(name.trim().toUpperCase().replace(" ", "_"));
                        changed = true;
                    }
                    if (existing.getActive() == null) {
                        existing.setActive(true);
                        changed = true;
                    }
                    return changed ? roleRepository.save(existing) : existing;
                })
                .orElseGet(() -> roleRepository.save(RoleEntity.builder()
                        .name(name)
                        .code(name.trim().toUpperCase().replace(" ", "_"))
                        .active(true)
                        .build()));
    }

    private void assignRole(UserEntity user, RoleEntity role) {
        userRoleRepository.findByUserIdAndRoleId(user.getId(), role.getId())
                .orElseGet(() -> userRoleRepository.save(UserRoleEntity.builder().user(user).role(role).build()));
    }
}
