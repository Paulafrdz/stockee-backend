package dev.paula.stockee_backend.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendPasswordResetEmail_shouldSendEmailWithCorrectContent() {

        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String email = "test@test.com";
        String token = "abc123";

        // Act
        emailService.sendPasswordResetEmail(email, token);

        // Assert
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendPasswordResetEmail_shouldReplaceTokenInTemplate() {

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String email = "user@test.com";
        String token = "TOKEN123";

        emailService.sendPasswordResetEmail(email, token);

        verify(mailSender).send(mimeMessage);
    }

     @Test
    void sendPasswordResetEmail_shouldSendEmail() {

        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendPasswordResetEmail("test@test.com", "token123");

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}