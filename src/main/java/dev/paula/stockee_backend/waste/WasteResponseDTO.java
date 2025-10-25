package dev.paula.stockee_backend.waste;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WasteResponseDTO {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private Double quantity;
    private String unit;
    private String reason;
    private String details;
    private LocalDateTime timestamp;
}