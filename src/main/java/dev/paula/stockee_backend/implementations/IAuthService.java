package dev.paula.stockee_backend.implementations;

public interface IAuthService<T, R> {
    R login(T request);
    void logout(String token);
}
