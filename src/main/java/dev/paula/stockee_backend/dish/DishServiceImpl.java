package dev.paula.stockee_backend.dish;

import dev.paula.stockee_backend.implementations.IDishService;
import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements IDishService {

    private final DishRepository dishRepository;
    private final StockRepository stockRepository;

    @Override
    public DishResponseDTO createDish(DishRequestDTO request) {

        DishEntity dish = new DishEntity();
        dish.setName(request.getName());
        dish.setDescription(request.getDescription());
        dish.setIcon(request.getIcon());

        List<DishIngredientEntity> ingredients = request.getIngredients().stream()
                .map(dto -> {
                    StockEntity ingredient = stockRepository.findById(dto.getInventoryItemId())
                            .orElseThrow(() -> new RuntimeException("Ingredient not found"));

                    DishIngredientEntity dishIngredient = new DishIngredientEntity();
                    dishIngredient.setDish(dish);
                    dishIngredient.setIngredient(ingredient);
                    dishIngredient.setQuantity(dto.getQuantity());
                    dishIngredient.setUnit(dto.getUnit());

                    return dishIngredient;
                })
                .collect(Collectors.toList());

        dish.setIngredients(ingredients);

        DishEntity saved = dishRepository.save(dish);

        return convertToDTO(saved);
    }

    @Override
    public List<DishResponseDTO> getAllDishes() {
        return dishRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDish(Long dishId) {
        dishRepository.deleteById(dishId);
    }

    
    private DishResponseDTO convertToDTO(DishEntity dish) {

        List<DishResponseDTO.DishIngredientResponseDTO> ingredients =
                dish.getIngredients().stream()
                        .map(ing -> new DishResponseDTO.DishIngredientResponseDTO(
                                ing.getIngredient().getId(),
                                ing.getIngredient().getName(),
                                ing.getQuantity(),
                                ing.getUnit()
                        ))
                        .collect(Collectors.toList());

        return new DishResponseDTO(
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getIcon(),
                ingredients
        );
    }
}
