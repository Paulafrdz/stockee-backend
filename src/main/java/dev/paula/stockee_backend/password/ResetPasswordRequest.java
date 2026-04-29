package dev.paula.stockee_backend.password;

public record ResetPasswordRequest(String token, String newPassword) {
    
}
