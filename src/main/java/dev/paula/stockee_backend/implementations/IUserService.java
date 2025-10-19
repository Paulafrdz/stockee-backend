package dev.paula.stockee_backend.implementations;

import java.util.List;

public interface IUserService<T, S> {

    S register(T request);
    List<S> getEntities();
    S getById(Long id);
    S updateEntity(Long id, T dto);
    void deleteEntity(Long id);
}
