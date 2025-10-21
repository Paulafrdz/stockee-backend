package dev.paula.stockee_backend.stock;

import org.springframework.web.bind.annotation.*;

import dev.paula.stockee_backend.implementations.IStockService;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final IStockService stockService;

    public StockController(IStockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<StockEntity> getAll() {
        return stockService.getAll();
    }

    @PostMapping
    public StockEntity addItem(@RequestBody StockEntity item) {
        return stockService.addItem(item);
    }

    @PutMapping("/{id}")
    public StockEntity updateItem(@PathVariable Long id, @RequestBody StockEntity item) {
        return stockService.updateItem(id, item);
    }

    @PutMapping("/{id}/stock")
    public StockEntity updateStock(@PathVariable Long id, @RequestParam double newStock) {
        return stockService.updateStock(id, newStock);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        stockService.deleteItem(id);
    }
}