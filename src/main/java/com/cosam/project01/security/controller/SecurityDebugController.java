package com.cosam.project01.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/security")
public class SecurityDebugController {

    @GetMapping("/whoami")
    public ResponseEntity<Map<String, Object>> whoami(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("authenticated", authentication != null && authentication.isAuthenticated());
        response.put("principal", authentication != null ? String.valueOf(authentication.getName()) : null);
        response.put("authorities", authentication != null
                ? authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
                : java.util.List.of());
        response.put("authorizationHeaderPresent", request.getHeader("Authorization") != null);
        response.put("xForwardedAuthorizationHeaderPresent", request.getHeader("X-Forwarded-Authorization") != null);
        response.put("path", request.getRequestURI());
        return ResponseEntity.ok(response);
    }
}
