package dev.paula.stockee_backend.implementations;

import java.util.List;

import dev.paula.stockee_backend.user.UserEntity;

public interface IUserService<T, S> {

    S register(T request);
    List<S> getEntities();
    S getById(Long id);
    S updateEntity(Long id, T dto);
    void deleteEntity(Long id);
    boolean getOnboardingStatus(String email);
    void completeOnboarding(String email);
    UserEntity findByEmail(String email);
}
