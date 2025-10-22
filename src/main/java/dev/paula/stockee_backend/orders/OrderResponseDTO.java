package dev.paula.stockee_backend.orders;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private String ingredientName;
    private Double currentStock;
    private Double minimumStock;
    private Double weeklyUsage;
    private Double recommendedQuantity;
    private String status;
    private String createdAt;
}
