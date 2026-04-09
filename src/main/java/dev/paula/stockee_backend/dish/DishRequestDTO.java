package dev.paula.stockee_backend.dish;

import lombok.Data;
import java.util.List;

@Data
public class DishRequestDTO {

    private String name;
    private String description;
    private String icon;

    private List<DishIngredientDTO> ingredients;

    @Data
    public static class DishIngredientDTO {
        private Long ingredientId;
        private Double quantity;
        private String unit;
    }
}
