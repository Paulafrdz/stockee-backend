package dev.paula.stockee_backend.security;

import dev.paula.stockee_backend.user.UserEntity;
import dev.paula.stockee_backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtService jwtService;

    private Jwt mockJwt(String token) {
        return Jwt.withTokenValue(token)
                .header("alg", "HS512")
                .claim("scope", "ROLE_USER")
                .build();
    }

    @Test
    void generateToken_ShouldReturnToken() {
        String email = "paula@example.com";
        String username = "paula";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        when(authentication.getName()).thenReturn(email);
        when(authentication.getAuthorities()).thenReturn(
                (List) List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(mockJwt("token123"));

        String result = jwtService.generateToken(authentication);

        assertEquals("token123", result);
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void generateToken_WhenUserNotFound_ShouldUseEmailAsUsername() {
        String email = "unknown@mail.com";

        when(authentication.getName()).thenReturn(email);
        when(authentication.getAuthorities()).thenReturn(
                (List) List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(mockJwt("token456"));

        String result = jwtService.generateToken(authentication);

        assertEquals("token456", result);
    }

    @Test
    void generateToken_ShouldHandleEmptyRoles() {
        String email = "test@mail.com";

        when(authentication.getName()).thenReturn(email);
        when(authentication.getAuthorities()).thenReturn(List.of());

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(mockJwt("token-empty"));

        String result = jwtService.generateToken(authentication);

        assertEquals("token-empty", result);
    }
}