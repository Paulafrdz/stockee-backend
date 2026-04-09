package dev.paula.stockee_backend.sales;

import lombok.Data;

import java.util.List;


@Data
public class SaleRequestDTO {
    
    private List<SaleLineDTO> lines;
    
    @Data
    public static class SaleLineDTO {
        private Long dishId;
        private Integer quantity;
    }
}

