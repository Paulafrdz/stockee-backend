package dev.paula.stockee_backend.register;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import dev.paula.stockee_backend.implementations.IRegisterService;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.http.HttpStatus;
import static org.mockito.Mockito.*;

class RegisterControllerTest {

    @Mock
    private IRegisterService<RegisterRequestDTO, RegisterResponseDTO> registerService;

    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registerController = new RegisterController(registerService);
    }

    @Test
void testRegister_ReturnsCreatedResponse() {
    RegisterRequestDTO request = new RegisterRequestDTO("paula", "paula@example.com", "1234");
    RegisterResponseDTO responseDTO = new RegisterResponseDTO(1L, "paula", "paula@example.com");

    when(registerService.register(request)).thenReturn(responseDTO);

    ResponseEntity<RegisterResponseDTO> response = registerController.register(request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    assertThat(response.getBody()).isEqualTo(responseDTO);

    verify(registerService).register(request);
}
}
