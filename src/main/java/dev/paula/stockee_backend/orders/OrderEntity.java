package dev.paula.stockee_backend.orders;

import dev.paula.stockee_backend.stock.StockEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el ingrediente asociado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private StockEntity ingredient;

    @Column(nullable = false)
    private Double currentStock;

    @Column(nullable = false)
    private Double minimumStock;

    @Column(nullable = false)
    private Double weeklyUsage;

    @Column(nullable = false)
    private Double recommendedQuantity;

    @Column(nullable = false)
    private String status; 

    @Column(nullable = false)
    private String createdAt;
}
