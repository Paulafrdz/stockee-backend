package dev.paula.stockee_backend.register;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.paula.stockee_backend.user.UserEntity;
import dev.paula.stockee_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements InterfaceRegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        
        // 1️⃣ Validar duplicados
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email ya registrado");
        }

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username ya registrado");
        }

        // 2️⃣ Crear entidad de usuario
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        // 3️⃣ Guardar usuario en base de datos
        UserEntity savedUser = userRepository.save(user);

        // 4️⃣ Devolver respuesta con DTO inmutable (record)
        return new RegisterResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }
}
