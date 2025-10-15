package dev.paula.stockee_backend.register;

import org.springframework.web.bind.annotation.*;

import dev.paula.stockee_backend.implementations.IRegisterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegisterController {

    private final IRegisterService<RegisterRequestDTO, RegisterResponseDTO> registerService;

    @PostMapping
    public RegisterResponseDTO register(@RequestBody RegisterRequestDTO request) {
        return registerService.register(request);
    }
}
