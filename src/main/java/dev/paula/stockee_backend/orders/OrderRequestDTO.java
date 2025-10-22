package dev.paula.stockee_backend.orders;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private Long ingredientId;
    private Double currentStock;
    private Double minimumStock;
    private Double weeklyUsage;
    private Double recommendedQuantity;
    private String status;
}
