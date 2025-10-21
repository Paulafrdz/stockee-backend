package dev.paula.stockee_backend.implementations;

import java.util.List;

import dev.paula.stockee_backend.stock.StockEntity;

public interface IStockService {

    List<StockEntity> getAll();
    StockEntity addItem(StockEntity item);
    StockEntity updateStock(Long id, double newStock);
    void deleteItem(Long id);
}
