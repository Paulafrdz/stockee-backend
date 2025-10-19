package dev.paula.stockee_backend.user;

import org.springframework.stereotype.Service;

import dev.paula.stockee_backend.implementations.IUserService;

@Service
public interface UserService extends IUserService<UserRequestDTO, UserResponseDTO>{

}
