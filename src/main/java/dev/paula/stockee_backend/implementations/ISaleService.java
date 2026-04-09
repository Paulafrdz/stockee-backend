package dev.paula.stockee_backend.implementations;

import java.util.List;

import dev.paula.stockee_backend.sales.SaleRequestDTO;
import dev.paula.stockee_backend.sales.SaleResponseDTO;

public interface ISaleService {

    SaleResponseDTO createSale(SaleRequestDTO request);
    List<SaleResponseDTO> getAllSales();
    void deleteSale(Long saleId);
}
