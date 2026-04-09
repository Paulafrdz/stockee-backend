package dev.paula.stockee_backend.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SaleResponseDTO {
    private Long id;
    private LocalDateTime date;

    private List<SaleLineResponseDTO> lines;

    @Data
    @AllArgsConstructor
    public static class SaleLineResponseDTO {
        private Long dishId;
        private String dishName;
        private String dishIcon;
        private Integer quantity;
    }
}
