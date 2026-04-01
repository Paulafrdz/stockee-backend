package dev.paula.stockee_backend.dish;

import dev.paula.stockee_backend.stock.StockEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dish_ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishIngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double quantity;

    private String unit;

    @ManyToOne
    @JoinColumn(name = "dish_id")
    private DishEntity dish;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private StockEntity ingredient;
}
