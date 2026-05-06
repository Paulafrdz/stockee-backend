package dev.paula.stockee_backend.security;

import dev.paula.stockee_backend.user.UserEntity;
import dev.paula.stockee_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public UserEntity get() {
        String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado: " + email));
    } 
}
