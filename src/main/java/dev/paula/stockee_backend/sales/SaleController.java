package dev.paula.stockee_backend.sales;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import dev.paula.stockee_backend.implementations.ISaleService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {
    private final ISaleService saleService;

    @PostMapping
    public SaleResponseDTO createSale(@RequestBody SaleRequestDTO request) {
        return saleService.createSale(request);
    }

    @GetMapping
    public List<SaleResponseDTO> getAllSales() {
        return saleService.getAllSales();
    }

    @DeleteMapping("/{saleId}")
    public void deleteSale(@PathVariable Long saleId) {
        saleService.deleteSale(saleId);
    }
}
