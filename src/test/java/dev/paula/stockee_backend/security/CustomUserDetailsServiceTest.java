package dev.paula.stockee_backend.security;

import dev.paula.stockee_backend.user.UserEntity;
import dev.paula.stockee_backend.user.UserRepository;
import dev.paula.stockee_backend.role.RoleEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Given
        String email = "paula@example.com";
        
        RoleEntity userRole = new RoleEntity();
        userRole.setName("ROLE_USER");
        
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName("ROLE_ADMIN");
        
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword("encodedPassword");
        userEntity.setRoles(Set.of(userRole, adminRole));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertEquals(2, userDetails.getAuthorities().size());
        
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ShouldThrowException() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(email)
        );
        
        assertEquals("Usuario no encontrado con email: " + email, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_WhenUserHasNoRoles_ShouldReturnEmptyAuthorities() {
        // Given
        String email = "noreoles@example.com";
        
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword("password");
        userEntity.setRoles(Set.of()); // Sin roles

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().isEmpty());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
