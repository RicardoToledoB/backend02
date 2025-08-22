
package com.cosam.project01.security;

import com.cosam.project01.entity.UserEntity;
import com.cosam.project01.repository.UserRepository;
import com.cosam.project01.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // NOTA: el parámetro "username" de la interfaz lo usamos como "email"
        UserEntity u = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado"));

        List<SimpleGrantedAuthority> authorities = userRoleRepository.findRoleNamesByUserId(u.getId())
                .stream()
                .map(name -> {
                    String n = name == null ? "" : name.trim().toUpperCase();
                    if (n.startsWith("ROLE_")) n = n.substring(5);
                    return new SimpleGrantedAuthority("ROLE_" + n);
                })
                .toList();

        // MUY IMPORTANTE: el "username" del UserDetails será el EMAIL,
        // para que coincida con el 'sub' del JWT.
        return User.withUsername(u.getEmail())
                .password(u.getPassword()) // debe estar en BCrypt
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
