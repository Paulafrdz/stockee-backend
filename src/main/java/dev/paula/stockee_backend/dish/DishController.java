package dev.paula.stockee_backend.dish;

import dev.paula.stockee_backend.implementations.IDishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishController {

    private final IDishService dishService;

    @PostMapping
    public DishResponseDTO createDish(@RequestBody DishRequestDTO request) {
        return dishService.createDish(request);
    }

    @GetMapping
    public List<DishResponseDTO> getAllDishes() {
        return dishService.getAllDishes();
    }

    @DeleteMapping("/{dishId}")
    public void deleteDish(@PathVariable Long dishId) {
        dishService.deleteDish(dishId);
    }

    @PutMapping("/{dishId}")
    public DishResponseDTO updateDish(@PathVariable Long dishId, @RequestBody DishRequestDTO request) {
        return dishService.updateDish(dishId, request);
    }
}
