package dev.paula.stockee_backend.waste;

import dev.paula.stockee_backend.stock.StockEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import dev.paula.stockee_backend.user.UserEntity;

@Entity
@Table(name = "waste")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private StockEntity ingredient;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public WasteEntity(StockEntity ingredient, Double quantity, String unit, 
                      String reason, String details) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
        this.reason = reason;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}