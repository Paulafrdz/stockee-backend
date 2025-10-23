package dev.paula.stockee_backend.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemHistoryResponseDTO {
    private String name;
    private Double quantity;
    private String unit;
}


