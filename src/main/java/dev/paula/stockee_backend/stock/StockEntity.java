package dev.paula.stockee_backend.stock;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "stock_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double currentStock;

    private double minimumStock;

    private String unit;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime lastUpdate;

    
}
