package dev.paula.stockee_backend.register;

import dev.paula.stockee_backend.user.UserEntity;
import dev.paula.stockee_backend.user.UserRepository;
import dev.paula.stockee_backend.role.RoleEntity;
import dev.paula.stockee_backend.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterServiceImpl registerService;

    @Test
    void register_ShouldCreateNewUser() {
        // Given
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("paula", "paula@example.com", "password123");
        
        RoleEntity userRole = new RoleEntity();
        userRole.setName("ROLE_USER");
        userRole.setUsers(new HashSet<>());
        
        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setUsername("paula");
        savedUser.setEmail("paula@example.com");
        savedUser.setPassword("encodedPassword");

        when(userRepository.findByEmail("paula@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("paula")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // When
        RegisterResponseDTO result = registerService.register(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("paula", result.username());
        assertEquals("paula@example.com", result.email());
        
        verify(userRepository, times(1)).findByEmail("paula@example.com");
        verify(userRepository, times(1)).findByUsername("paula");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowException() {
        // Given
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("paula", "existing@example.com", "password123");
        
        UserEntity existingUser = new UserEntity();
        existingUser.setEmail("existing@example.com");

        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> registerService.register(requestDTO));
        
        assertEquals("Email ya registrado", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("existing@example.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void register_WhenUsernameAlreadyExists_ShouldThrowException() {
        // Given
        RegisterRequestDTO requestDTO = new RegisterRequestDTO("existinguser", "paula@example.com", "password123");
        
        when(userRepository.findByEmail("paula@example.com")).thenReturn(Optional.empty());
        
        UserEntity existingUser = new UserEntity();
        existingUser.setUsername("existinguser");
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> registerService.register(requestDTO));
        
        assertEquals("Username ya registrado", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("existinguser");
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}