package dev.paula.stockee_backend.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.paula.stockee_backend.password.PasswordResetService;
import dev.paula.stockee_backend.password.ForgotPasswordRequest;
import dev.paula.stockee_backend.password.ResetPasswordRequest;

import dev.paula.stockee_backend.security.JwtService;


@RestController
@RequestMapping("${api-endpoint}/auth")
public class AuthController {

    private final JwtService tokenService;
    private final PasswordResetService passwordResetService;

    public AuthController(JwtService tokenService, PasswordResetService passwordResetService) {
        this.tokenService = tokenService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/token")
    public String token(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }
}
