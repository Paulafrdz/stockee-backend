package dev.paula.stockee_backend.stock;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import dev.paula.stockee_backend.lotes.LoteRepository; 
import dev.paula.stockee_backend.lotes.LoteEntity; 

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository repository;
    private final LoteRepository loteRepository;

    public StockServiceImpl(StockRepository repository, LoteRepository loteRepository) {
        this.repository = repository;
        this.loteRepository = loteRepository;
    }

    @Override
    public List<StockEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public StockEntity addItem(StockEntity item) {
        item.setLastUpdate(LocalDateTime.now());
        StockEntity saved = repository.save(item);

        if (saved.getCurrentStock() > 0 && saved.getShelfLifeDays() != null){
            LocalDate today = LocalDate.now();
            LoteEntity lote = LoteEntity.builder()
                .stock(saved)
                .quantity(saved.getCurrentStock())
                .unit(saved.getUnit())
                .orderDate(today)
                .expiryDate(today.plusDays(saved.getShelfLifeDays()))
                .build();
            loteRepository.save(lote);
        }
        return saved;
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
    public StockEntity updateItem(Long id, StockEntity updatedItem) {
        StockEntity existingItem = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        
        boolean hadShelfLifeBefore = existingItem.getShelfLifeDays() != null;
        boolean hasShelfLifeNow = updatedItem.getShelfLifeDays() != null;

        existingItem.setName(updatedItem.getName());
        existingItem.setCurrentStock(updatedItem.getCurrentStock());
        existingItem.setMinimumStock(updatedItem.getMinimumStock());
        existingItem.setUnit(updatedItem.getUnit());
        existingItem.setLastUpdate(LocalDateTime.now());
        existingItem.setShelfLifeDays(updatedItem.getShelfLifeDays());
        
        StockEntity saved =repository.save(existingItem);

        if (!hadShelfLifeBefore  && hasShelfLifeNow && saved.getShelfLifeDays() != null && saved.getCurrentStock() > 0 ) {
            LocalDate today = LocalDate.now();

            LoteEntity lote = LoteEntity.builder()
                .stock(saved)
                .quantity(saved.getCurrentStock())
                .unit(saved.getUnit())
                .orderDate(today)
                .expiryDate(today.plusDays(saved.getShelfLifeDays()))
                .build();

            loteRepository.save(lote);
        }

        return repository.save(existingItem);
    }

    @Override
    public void deleteItem(Long id) {
        repository.deleteById(id);
    }
}