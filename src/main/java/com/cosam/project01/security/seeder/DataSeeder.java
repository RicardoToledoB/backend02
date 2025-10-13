package com.cosam.project01.security.seeder;


import com.cosam.project01.entity.*;
import com.cosam.project01.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JpaRepository<RoleEntity, Integer> roleRepo;
    private final UserRoleRepository userRoleRepository;

    @Override
    public void run(String... args) {
        RoleEntity admin = roleRepo.save(RoleEntity.builder().name("ADMIN").build());
        RoleEntity administrativo = roleRepo.save(RoleEntity.builder().name("ADMINISTRATIVO").build());

        // Crea usuarios (password BCrypt)
        UserEntity u1 = userRepository.save(UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin123$"))
                .email("admin@demo.com")
                .firstName("Admin")
                .build());

        UserEntity u2 = userRepository.save(UserEntity.builder()
                .username("operador")
                .password(passwordEncoder.encode("Operador123$"))
                .email("operador@demo.com")
                .firstName("Operador")
                .build());

        // Asigna roles
        userRoleRepository.save(UserRoleEntity.builder().user(u1).role(admin).build());
        userRoleRepository.save(UserRoleEntity.builder().user(u2).role(administrativo).build());
    }
}