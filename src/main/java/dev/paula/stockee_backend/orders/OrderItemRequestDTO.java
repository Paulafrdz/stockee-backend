package dev.paula.stockee_backend.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDTO {
    private Long id;                        
    private String name;
    private Double recommendedQuantity;
    private String unit;
}