package dev.paula.stockee_backend.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink =  "http://localhost:5173/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Restablecer contraseña stockee.");
        message.setText("Hola,\n\n" +
            "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
            "Pulsa el siguiente enlace para crear una nueva contraseña:\n" +
            resetLink + "\n\n" +
            "Este enlace expira en 30 minutos.\n\n" +
            "Si no solicitaste este cambio, ignora este correo.\n\n" +
            "— El equipo de Stockeo"
        );

        mailSender.send(message);
    }
    
}
