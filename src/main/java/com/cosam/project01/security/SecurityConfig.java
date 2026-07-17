package com.cosam.project01.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final DatabaseUserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable())
                // Si prefieres mantener CSRF pero ignorar /auth/**, usa:
                // .csrf(csrf -> csrf.ignoringRequestMatchers("/auth/**", "/docs/**", "/api-docs/**"))

                // ✅ CORS habilitado (usa tu bean CorsConfigurationSource)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/docs/**", "/api-docs/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                        ).permitAll()
                        // Preflight CORS para PUT/PATCH/DELETE desde Angular
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // H2 console (opcional para pruebas):
                        .requestMatchers("/h2-console/**").permitAll()
                        // Mantenedores de catálogos de Demanda.
                        // Se habilita explícitamente para ADMIN, ADMINISTRATIVO y SUPERVISOR,
                        // evitando 403 en rutas como /api/v1/demand/maintainers/episodeTypes.
                        .requestMatchers("/api/v1/time/server").permitAll()
                        // Módulo Demanda completo: episodios, longitudinal, eventos, referencias, dashboard,
                        // documentos, catálogos y endpoints auxiliares. Se autoriza explícitamente para evitar
                        // 403 cuando el token trae ROLE_ADMIN / ROLE_ADMINISTRATIVO / ROLE_SUPERVISOR / ROLE_PROFESIONAL.
                        .requestMatchers("/api/v1/demand/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_ADMINISTRATIVO", "ROLE_SUPERVISOR", "ROLE_PROFESIONAL")
                        .requestMatchers("/api/v1/professions/**", "/api/v1/int_prevs/**", "/api/v1/conv_prevs/**", "/api/v1/program_professionals/**", "/api/v1/results/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_ADMINISTRATIVO", "ROLE_SUPERVISOR", "ROLE_PROFESIONAL")
                        .requestMatchers("/api/v1/postulants/searchByRut")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_ADMINISTRATIVO", "ROLE_SUPERVISOR", "ROLE_PROFESIONAL")
                        .anyRequest().authenticated()
                );

        // H2 console (opcional):
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
