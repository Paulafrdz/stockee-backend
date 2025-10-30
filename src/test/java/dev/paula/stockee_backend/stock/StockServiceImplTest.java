package dev.paula.stockee_backend.stock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository repository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void getAll_ShouldReturnAllItems() {
        // Given
        StockEntity item1 = new StockEntity();
        item1.setId(1L);
        StockEntity item2 = new StockEntity();
        item2.setId(2L);
        List<StockEntity> expectedItems = List.of(item1, item2);

        when(repository.findAll()).thenReturn(expectedItems);

        // When
        List<StockEntity> result = stockService.getAll();

        // Then
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void addItem_ShouldSetLastUpdateAndSave() {
        StockEntity inputItem = new StockEntity();
        inputItem.setName("Test Item");
        inputItem.setCurrentStock(100.0);

        when(repository.save(any(StockEntity.class))).thenAnswer(invocation -> {
            StockEntity itemToSave = invocation.getArgument(0);
            itemToSave.setId(1L);
            return itemToSave; 
        });

        StockEntity result = stockService.addItem(inputItem);

        assertNotNull(result.getLastUpdate(), "lastUpdate debería estar establecido");
        assertEquals(1L, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals(100.0, result.getCurrentStock());

        assertTrue(result.getLastUpdate().isAfter(LocalDateTime.now().minusSeconds(5)));
        assertTrue(result.getLastUpdate().isBefore(LocalDateTime.now().plusSeconds(1)));

        verify(repository, times(1)).save(inputItem);
    }

    @Test
    void updateStock_WhenItemExists_ShouldUpdateStockAndLastUpdate() {
        // Given
        Long itemId = 1L;
        double newStock = 150.0;

        StockEntity existingItem = new StockEntity();
        existingItem.setId(itemId);
        existingItem.setCurrentStock(100.0);
        existingItem.setLastUpdate(LocalDateTime.now().minusDays(1));

        when(repository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(repository.save(any(StockEntity.class))).thenReturn(existingItem);

        // When
        StockEntity result = stockService.updateStock(itemId, newStock);

        // Then
        assertEquals(newStock, result.getCurrentStock());
        assertTrue(result.getLastUpdate().isAfter(LocalDateTime.now().minusMinutes(1)));
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(1)).save(existingItem);
    }

    @Test
    void updateStock_WhenItemNotFound_ShouldThrowException() {
        // Given
        Long itemId = 999L;
        when(repository.findById(itemId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> stockService.updateStock(itemId, 100.0));

        assertEquals("Item no encontrado", exception.getMessage());
        verify(repository, times(1)).findById(itemId);
        verify(repository, never()).save(any());
    }

    @Test
    void updateItem_WhenItemExists_ShouldUpdateAllFields() {
        // Given
        Long itemId = 1L;
        StockEntity existingItem = new StockEntity();
        existingItem.setId(itemId);
        existingItem.setName("Old Name");
        existingItem.setCurrentStock(50.0);
        existingItem.setMinimumStock(10.0);
        existingItem.setUnit("kg");
        existingItem.setLastUpdate(LocalDateTime.now().minusDays(1));

        StockEntity updatedItem = new StockEntity();
        updatedItem.setName("New Name");
        updatedItem.setCurrentStock(75.0);
        updatedItem.setMinimumStock(20.0);
        updatedItem.setUnit("units");

        when(repository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(repository.save(any(StockEntity.class))).thenReturn(existingItem);

        // When
        StockEntity result = stockService.updateItem(itemId, updatedItem);

        // Then
        assertEquals("New Name", result.getName());
        assertEquals(75.0, result.getCurrentStock());
        assertEquals(20.0, result.getMinimumStock());
        assertEquals("units", result.getUnit());
        assertTrue(result.getLastUpdate().isAfter(LocalDateTime.now().minusMinutes(1)));
        verify(repository, times(1)).findById(itemId);
        verify(repository, times(1)).save(existingItem);
    }

    @Test
    void deleteItem_ShouldCallRepositoryDelete() {
        // Given
        Long itemId = 1L;
        doNothing().when(repository).deleteById(itemId);

        // When
        stockService.deleteItem(itemId);

        // Then
        verify(repository, times(1)).deleteById(itemId);
    }
}