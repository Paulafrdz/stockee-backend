package dev.paula.stockee_backend.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import lombok.RequiredArgsConstructor;

import dev.paula.stockee_backend.user.UserRepository;
import dev.paula.stockee_backend.user.UserEntity;
import dev.paula.stockee_backend.role.RoleEntity;
import dev.paula.stockee_backend.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponseDTO login(AuthRequestDTO request) {
        // Autentication
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // user
        UserEntity user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // token
        String token = jwtService.generateToken(user);
        String role = user.getRoles().stream()
            .map(RoleEntity::getName)
            .findFirst()
            .orElse("ROLE_USER");

        return new AuthResponseDTO(token, user.getUsername(), role);
    }

    @Override
    public void logout(String token) {
    }
}
