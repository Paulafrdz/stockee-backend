package dev.paula.stockee_backend.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import dev.paula.stockee_backend.security.JwtService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtService tokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @Test
    void token_WithValidAuthentication_ShouldReturnToken() {
        // Arrange
        String expectedToken = "test.jwt.token";
        when(tokenService.generateToken(authentication)).thenReturn(expectedToken);

        // Act
        String result = authController.token(authentication);

        // Assert
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(tokenService, times(1)).generateToken(authentication);
    }

    @Test
    void token_WithNullAuthentication_ShouldCallServiceWithNull() {
        // Arrange
        String expectedToken = "test.jwt.token";
        when(tokenService.generateToken(null)).thenReturn(expectedToken);

        // Act
        String result = authController.token(null);

        // Assert
        assertNotNull(result);
        assertEquals(expectedToken, result);
        verify(tokenService, times(1)).generateToken(null);
    }

    @Test
    void constructor_ShouldInitializeDependencies() {
        // Arrange & Act
        AuthController controller = new AuthController(tokenService);

        // Assert
        assertNotNull(controller);
    }
}