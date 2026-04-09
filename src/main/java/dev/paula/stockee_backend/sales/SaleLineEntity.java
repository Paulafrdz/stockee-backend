package dev.paula.stockee_backend.sales;

import dev.paula.stockee_backend.dish.DishEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sales_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleLineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private SaleEntity sale;

    @ManyToOne
    @JoinColumn(name = "dish_id")
    private DishEntity dish;
}
