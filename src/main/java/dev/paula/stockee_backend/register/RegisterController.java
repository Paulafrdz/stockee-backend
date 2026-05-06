package dev.paula.stockee_backend.register;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.paula.stockee_backend.implementations.IRegisterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegisterController {

    private final IRegisterService<RegisterRequestDTO, RegisterResponseDTO> registerService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO dto) {
        RegisterResponseDTO response = registerService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(response);
    }
}
