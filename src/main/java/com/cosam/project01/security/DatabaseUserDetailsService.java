
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<SimpleGrantedAuthority> authorities = userRoleRepository.findRoleNamesByUserId(u.getId())
                .stream()
                .map(name -> new SimpleGrantedAuthority("ROLE_" + name.toUpperCase()))
                .toList();

        // Importante: password ya debe estar BCRYPT en la BD
        return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
