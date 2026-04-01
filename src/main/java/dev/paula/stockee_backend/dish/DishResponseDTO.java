package dev.paula.stockee_backend.dish;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DishResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String icon;

    private List<DishIngredientResponseDTO> ingredients;

    @Data
    @AllArgsConstructor
    public static class DishIngredientResponseDTO {
        private Long ingredientId;
        private String ingredientName;
        private Double quantity;
        private String unit;
    }
}
