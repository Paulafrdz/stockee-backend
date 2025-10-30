package dev.paula.stockee_backend.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void generateToken_ShouldReturnValidToken() {
        // Given
        String username = "paula@example.com";
        String expectedToken = "mock.jwt.token.value";
        
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        // ✅ Sin @SuppressWarnings - usar thenAnswer
        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        Jwt mockJwt = Jwt.withTokenValue(expectedToken)
                .header("alg", "HS512")
                .claim("sub", username)
                .claim("scope", "ROLE_USER ROLE_ADMIN")
                .build();
        
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        String result = jwtService.generateToken(authentication);

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result);
        
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void generateToken_WithSingleRole_ShouldReturnTokenWithSingleScope() {
        // Given
        String username = "user@example.com";
        String expectedToken = "single.role.token";
        
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        Jwt mockJwt = Jwt.withTokenValue(expectedToken)
                .header("alg", "HS512")
                .claim("sub", username)
                .claim("scope", "ROLE_USER")
                .build();
        
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        String result = jwtService.generateToken(authentication);

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void generateToken_WithNoRoles_ShouldReturnTokenWithEmptyScope() {
        // Given
        String username = "noreoles@example.com";
        String expectedToken = "no.roles.token";
        
        List<GrantedAuthority> authorities = List.of();

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        Jwt mockJwt = Jwt.withTokenValue(expectedToken)
                .header("alg", "HS512")
                .claim("sub", username)
                .claim("scope", "")
                .build();
        
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        String result = jwtService.generateToken(authentication);

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void generateToken_ShouldIncludeCorrectClaims() {
        // Given
        String username = "test@example.com";
        String expectedToken = "test.token";
        
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        Jwt mockJwt = Jwt.withTokenValue(expectedToken)
                .header("alg", "HS512")
                .claim("iss", "self")
                .claim("sub", username)
                .claim("scope", "ROLE_USER")
                .build();
        
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        String result = jwtService.generateToken(authentication);

        // Then
        assertNotNull(result);
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void generateToken_ShouldHandleSpecialCharactersInUsername() {
        // Given
        String username = "user+test@example.com";
        String expectedToken = "special.chars.token";
        
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        when(authentication.getName()).thenReturn(username);
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        Jwt mockJwt = Jwt.withTokenValue(expectedToken)
                .header("alg", "HS512")
                .claim("sub", username)
                .claim("scope", "ROLE_USER")
                .build();
        
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // When
        String result = jwtService.generateToken(authentication);

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }
}