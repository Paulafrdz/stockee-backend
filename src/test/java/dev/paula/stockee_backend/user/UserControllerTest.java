package dev.paula.stockee_backend.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        // given
        UserRequestDTO request = new UserRequestDTO("paula", "paula@example.com", "1234");
        UserResponseDTO response = new UserResponseDTO(1L, "paula", "paula@example.com");

        when(userService.register(any(UserRequestDTO.class))).thenReturn(response);

        // when
        ResponseEntity<UserResponseDTO> result = userController.register(request);

        // then
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(result.getBody()).username()).isEqualTo("paula");
    }

    @Test
    void testGetAllUsers() {
        List<UserResponseDTO> mockUsers = List.of(
                new UserResponseDTO(1L, "paula", "paula@example.com"),
                new UserResponseDTO(2L, "juan", "juan@example.com")
        );

        when(userService.getEntities()).thenReturn(mockUsers);

        ResponseEntity<List<UserResponseDTO>> result = userController.getAllUsers();

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(result.getBody())).hasSize(2);
        assertThat(Objects.requireNonNull(result.getBody()).get(0).username()).isEqualTo("paula");
    }

    @Test
    void testGetUserById() {
        UserResponseDTO mockUser = new UserResponseDTO(1L, "paula", "paula@example.com");
        when(userService.getById(1L)).thenReturn(mockUser);

        ResponseEntity<UserResponseDTO> result = userController.getUser(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(result.getBody()).email()).isEqualTo("paula@example.com");
    }

    @Test
    void testUpdateUser() {
        UserRequestDTO request = new UserRequestDTO("paula2", "paula2@example.com", "newpass");
        UserResponseDTO updated = new UserResponseDTO(1L, "paula2", "paula2@example.com");

        when(userService.updateEntity(1L, request)).thenReturn(updated);

        ResponseEntity<UserResponseDTO> result = userController.updateUser(1L, request);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(result.getBody()).username()).isEqualTo("paula2");
    }

    @Test
    void testDeleteUser() {
        ResponseEntity<Void> result = userController.deleteUser(1L);

        assertThat(result.getStatusCode().value()).isEqualTo(204);
    }
}
