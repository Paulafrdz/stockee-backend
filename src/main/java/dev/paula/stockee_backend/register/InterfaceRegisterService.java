package dev.paula.stockee_backend.register;

import org.springframework.stereotype.Service;

import dev.paula.stockee_backend.implementations.IRegisterService;

@Service
public interface InterfaceRegisterService extends IRegisterService<RegisterRequestDTO, RegisterResponseDTO>{

}
