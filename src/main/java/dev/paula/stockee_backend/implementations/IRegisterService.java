package dev.paula.stockee_backend.implementations;

import org.springframework.stereotype.Service;

@Service
public interface IRegisterService <T,S>{
    S register(T request);
}