package dev.paula.stockee_backend.password;

import dev.paula.stockee_backend.email.EmailService;
import dev.paula.stockee_backend.user.UserEntity;
import dev.paula.stockee_backend.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private UserEntity user;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("oldPassword");
    }

    @Test
    void requestPasswordReset_ShouldCreateTokenAndSendEmail() {

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset("test@test.com");

        verify(tokenRepository).save(any(PasswordResetTokenEntity.class));

        verify(emailService).sendPasswordResetEmail(
                eq("test@test.com"),
                anyString()
        );
    }

    @Test
    void requestPasswordReset_WhenUserDoesNotExist_ShouldDoNothing() {

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        passwordResetService.requestPasswordReset("unknown@test.com");

        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    void resetPassword_ShouldUpdatePasswordAndMarkTokenAsUsed() {

        PasswordResetTokenEntity token = PasswordResetTokenEntity.builder()
                .token("valid-token")
                .email("test@test.com")
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        when(tokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(token));

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encodedPassword");

        passwordResetService.resetPassword("valid-token", "newPassword");

        assertEquals("encodedPassword", user.getPassword());
        assertTrue(token.isUsed());

        verify(userRepository).save(user);
        verify(tokenRepository).save(token);
    }

    @Test
    void resetPassword_WhenTokenDoesNotExist_ShouldThrowException() {

        when(tokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword("invalid-token", "1234")
        );

        assertEquals("Token no válido", exception.getMessage());
    }

    @Test
    void resetPassword_WhenTokenAlreadyUsed_ShouldThrowException() {

        PasswordResetTokenEntity token = PasswordResetTokenEntity.builder()
                .token("used-token")
                .email("test@test.com")
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(true)
                .build();

        when(tokenRepository.findByToken("used-token"))
                .thenReturn(Optional.of(token));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword("used-token", "1234")
        );

        assertEquals("El token ya ha sido usado", exception.getMessage());
    }

    @Test
    void resetPassword_WhenTokenExpired_ShouldThrowException() {

        PasswordResetTokenEntity token = PasswordResetTokenEntity.builder()
                .token("expired-token")
                .email("test@test.com")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();

        when(tokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(token));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword("expired-token", "1234")
        );

        assertEquals("El token ha expirado", exception.getMessage());
    }
}