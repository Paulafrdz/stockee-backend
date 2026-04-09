package dev.paula.stockee_backend.implementations;

import dev.paula.stockee_backend.dish.DishRequestDTO;
import dev.paula.stockee_backend.dish.DishResponseDTO;

import java.util.List;

public interface IDishService {

    DishResponseDTO createDish(DishRequestDTO request);
    DishResponseDTO updateDish(Long dishId, DishRequestDTO request);
    List<DishResponseDTO> getAllDishes();
    void deleteDish(Long dishId);
}
