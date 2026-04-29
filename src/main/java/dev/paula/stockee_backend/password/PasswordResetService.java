package dev.paula.stockee_backend.password;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.paula.stockee_backend.user.UserRepository;
import dev.paula.stockee_backend.email.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();

            PasswordResetTokenEntity resetToken = PasswordResetTokenEntity.builder()
                .token(token)
                .email(email)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

            tokenRepository.save(resetToken);
            emailService.sendPasswordResetEmail(email, token);
        });
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetTokenEntity resetToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token no válido"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("El token ya ha sido usado");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        userRepository.findByEmail(resetToken.getEmail()).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        });

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}
