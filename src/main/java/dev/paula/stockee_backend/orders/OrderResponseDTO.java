package dev.paula.stockee_backend.orders;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private String name;
    private Double currentStock;
    private Double minimumStock;
    private Double recommendedQuantity;
    private Double weeklyUsage;
    private String unit;
    private String priority;                
    private LocalDateTime lastOrderDate;
}