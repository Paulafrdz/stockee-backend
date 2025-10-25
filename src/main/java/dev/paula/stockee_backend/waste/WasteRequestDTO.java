package dev.paula.stockee_backend.waste;

import lombok.Data;

@Data
public class WasteRequestDTO {
    private Long ingredientId;
    private Double quantity;
    private String unit;
    private String reason;
    private String details;
}