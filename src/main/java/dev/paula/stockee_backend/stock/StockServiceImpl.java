package dev.paula.stockee_backend.stock;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository repository;

    public StockServiceImpl(StockRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<StockEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public StockEntity addItem(StockEntity item) {
        item.setLastUpdate(LocalDateTime.now());
        return repository.save(item);
    }

    @Override
    public StockEntity updateStock(Long id, double newStock) {
        StockEntity item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        item.setCurrentStock(newStock);
        item.setLastUpdate(LocalDateTime.now());
        return repository.save(item);
    }

    @Override
    public void deleteItem(Long id) {
        repository.deleteById(id);
    }
}

