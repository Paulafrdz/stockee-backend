package dev.paula.stockee_backend.user;

import dev.paula.stockee_backend.role.RoleEntity;
import dev.paula.stockee_backend.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_ShouldCreateNewUser() {
        // Given
        UserRequestDTO requestDTO = new UserRequestDTO("paula", "paula@example.com", "password123");

        RoleEntity userRole = new RoleEntity();
        userRole.setName("ROLE_USER");

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("paula");
        userEntity.setEmail("paula@example.com");
        userEntity.setPassword("password123");

        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setUsername("paula");
        savedUser.setEmail("paula@example.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRoles(Set.of(userRole));

        UserResponseDTO expectedResponse = new UserResponseDTO(1L, "paula", "paula@example.com");

        // Configurar los mocks - usar any() en lugar de objetos específicos
        when(userMapper.toEntity(requestDTO)).thenReturn(userEntity);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(expectedResponse); // ✅ Usar any()

        // When
        UserResponseDTO result = userService.register(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("paula", result.username());
        assertEquals("paula@example.com", result.email());

        verify(userMapper, times(1)).toEntity(requestDTO);
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMapper, times(1)).toResponse(any(UserEntity.class)); // ✅ Usar any()
    }

    @Test
    void getEntities_ShouldReturnAllUsers() {
        // Given
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        UserResponseDTO user1Response = new UserResponseDTO(1L, "user1", "user1@example.com");
        UserResponseDTO user2Response = new UserResponseDTO(2L, "user2", "user2@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toResponse(any(UserEntity.class))) // ✅ Usar any() con respuestas condicionales
                .thenAnswer(invocation -> {
                    UserEntity entity = invocation.getArgument(0);
                    if (entity.getId().equals(1L)) {
                        return user1Response;
                    } else if (entity.getId().equals(2L)) {
                        return user2Response;
                    }
                    return null;
                });

        // When
        List<UserResponseDTO> result = userService.getEntities();

        // Then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("user1", result.get(0).username());
        assertEquals(2L, result.get(1).id());
        assertEquals("user2", result.get(1).username());

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(2)).toResponse(any(UserEntity.class));
    }

    @Test
    void deleteEntity_ShouldDeleteUser() {
        // Given
        Long userId = 1L;

        // ✅ CORREGIDO: Mockear existsById en lugar de findById
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // When
        userService.deleteEntity(userId);

        // Then
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteEntity_WhenUserNotFound_ShouldThrowException() {
        // Given
        Long userId = 999L;

        // ✅ CORREGIDO: Mockear existsById en lugar de findById
        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deleteEntity(userId));

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getById_WhenUserExists_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("paula");
        userEntity.setEmail("paula@example.com");

        UserResponseDTO expectedResponse = new UserResponseDTO(userId, "paula", "paula@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(expectedResponse);

        // When
        UserResponseDTO result = userService.getById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("paula", result.username());
        assertEquals("paula@example.com", result.email());

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toResponse(any(UserEntity.class));
    }

    @Test
    void updateEntity_ShouldUpdateUser() {
        // Given
        Long userId = 1L;
        UserRequestDTO requestDTO = new UserRequestDTO("paula_updated", "paula_updated@example.com", "newpassword");

        UserEntity existingUser = new UserEntity();
        existingUser.setId(userId);
        existingUser.setUsername("paula");
        existingUser.setEmail("paula@example.com");

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(userId);
        updatedUser.setUsername("paula_updated");
        updatedUser.setEmail("paula_updated@example.com");

        UserResponseDTO expectedResponse = new UserResponseDTO(userId, "paula_updated", "paula_updated@example.com");

        // NO OLVIDAR mockear findById para update también
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(expectedResponse);

        // When
        UserResponseDTO result = userService.updateEntity(userId, requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("paula_updated", result.username());
        assertEquals("paula_updated@example.com", result.email());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMapper, times(1)).toResponse(any(UserEntity.class));
    }
}