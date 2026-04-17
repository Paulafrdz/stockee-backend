package dev.paula.stockee_backend.lotes;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoteResponseDTO {
    private Long id;
    private double quantity;
    private String unit;
    private LocalDate orderDate;
    private LocalDate expiryDate;
}