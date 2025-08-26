package com.cosam.project01.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 🔒 Dominios permitidos (ajusta a los tuyos reales)
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:4200",
                "https://*.up.railway.app",
                "https://tu-frontend.example"
        ));

        // ✅ Métodos permitidos
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS","HEAD"));

        // 📩 Headers que el navegador puede ENVIAR (usados en el preflight)
        config.setAllowedHeaders(List.of(
                "Authorization","Content-Type","Accept","Origin","X-Requested-With"
        ));

        // 📤 Headers que el navegador puede LEER de la respuesta (opcional)
        config.setExposedHeaders(List.of("Authorization"));

        // 🍪 Si usas cookies/sesión entre orígenes
        config.setAllowCredentials(true);

        // ⏱️ Cachea la respuesta del preflight en el navegador
        config.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta config a todos los endpoints
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
