package dev.paula.stockee_backend.waste;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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