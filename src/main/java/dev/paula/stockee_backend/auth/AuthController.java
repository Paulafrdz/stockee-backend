package dev.paula.stockee_backend.auth;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.paula.stockee_backend.security.JwtService;

@RestController
@RequestMapping("${api-endpoint}/auth")
public class AuthController {

    private final JwtService tokenService;

    public AuthController(JwtService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public String token(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }
}
