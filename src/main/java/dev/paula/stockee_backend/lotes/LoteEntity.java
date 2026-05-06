package dev.paula.stockee_backend.lotes;

import dev.paula.stockee_backend.stock.StockEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import dev.paula.stockee_backend.user.UserEntity;

@Entity
@Table(name = "lotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private StockEntity stock;

    private double quantity;

    private String unit;

    private LocalDate orderDate;

    private LocalDate expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}