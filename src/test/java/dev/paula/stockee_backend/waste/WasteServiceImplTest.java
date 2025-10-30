package dev.paula.stockee_backend.waste;

import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;
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
class WasteServiceImplTest {

    @Mock
    private WasteRepository wasteRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private WasteServiceImpl wasteService;

    @Test
    void registerWaste_ShouldRegisterWasteAndUpdateStock() {
        // Given
        WasteRequestDTO wasteRequest = new WasteRequestDTO();
        wasteRequest.setIngredientId(1L);
        wasteRequest.setQuantity(5.0);
        wasteRequest.setUnit("kg");
        wasteRequest.setReason("Caducado");
        wasteRequest.setDetails("Producto vencido");

        StockEntity ingredient = new StockEntity();
        ingredient.setId(1L);
        ingredient.setName("Harina");
        ingredient.setCurrentStock(20.0);
        ingredient.setUnit("kg");

        WasteEntity wasteEntity = new WasteEntity(
            ingredient,
            5.0,
            "kg",
            "Caducado",
            "Producto vencido"
        );
        wasteEntity.setId(1L);
        wasteEntity.setTimestamp(LocalDateTime.now());

        when(stockRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(wasteRepository.save(any(WasteEntity.class))).thenReturn(wasteEntity);

        // When
        WasteResponseDTO result = wasteService.registerWaste(wasteRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getIngredientId());
        assertEquals("Harina", result.getIngredientName());
        assertEquals(5.0, result.getQuantity());
        assertEquals("kg", result.getUnit());
        assertEquals("Caducado", result.getReason());
        assertEquals("Producto vencido", result.getDetails());
        assertNotNull(result.getTimestamp());

        // Verificar que se actualizó el stock
        assertEquals(15.0, ingredient.getCurrentStock()); // 20.0 - 5.0 = 15.0
        verify(stockRepository, times(1)).save(ingredient);
        verify(wasteRepository, times(1)).save(any(WasteEntity.class));
    }

    @Test
    void registerWaste_WhenIngredientNotFound_ShouldThrowException() {
        // Given
        WasteRequestDTO wasteRequest = new WasteRequestDTO();
        wasteRequest.setIngredientId(999L);
        wasteRequest.setQuantity(5.0);
        wasteRequest.setUnit("kg");
        wasteRequest.setReason("Caducado");
        wasteRequest.setDetails("Producto vencido");

        when(stockRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> wasteService.registerWaste(wasteRequest));

        assertEquals("Ingrediente no encontrado", exception.getMessage());
        verify(stockRepository, never()).save(any());
        verify(wasteRepository, never()).save(any());
    }

    @Test
    void registerWaste_WhenInsufficientStock_ShouldThrowException() {
        // Given
        WasteRequestDTO wasteRequest = new WasteRequestDTO();
        wasteRequest.setIngredientId(1L);
        wasteRequest.setQuantity(25.0);
        wasteRequest.setUnit("kg");
        wasteRequest.setReason("Caducado");
        wasteRequest.setDetails("Producto vencido");

        StockEntity ingredient = new StockEntity();
        ingredient.setId(1L);
        ingredient.setCurrentStock(20.0);

        when(stockRepository.findById(1L)).thenReturn(Optional.of(ingredient));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> wasteService.registerWaste(wasteRequest));

        assertEquals("Stock insuficiente para registrar el desperdicio", exception.getMessage());
        verify(stockRepository, never()).save(any());
        verify(wasteRepository, never()).save(any());
    }

    @Test
    void getAllWaste_ShouldReturnAllWasteRecords() {
        // Given
        StockEntity ingredient1 = new StockEntity();
        ingredient1.setId(1L);
        ingredient1.setName("Harina");

        StockEntity ingredient2 = new StockEntity();
        ingredient2.setId(2L);
        ingredient2.setName("Azúcar");

        WasteEntity waste1 = new WasteEntity(ingredient1, 2.0, "kg", "Derrame", "Accidente");
        waste1.setId(1L);
        waste1.setTimestamp(LocalDateTime.now());

        WasteEntity waste2 = new WasteEntity(ingredient2, 1.0, "kg", "Caducado", "Vencimiento");
        waste2.setId(2L);
        waste2.setTimestamp(LocalDateTime.now());

        when(wasteRepository.findAll()).thenReturn(List.of(waste1, waste2));

        // When
        List<WasteResponseDTO> result = wasteService.getAllWaste();

        // Then
        assertEquals(2, result.size());
        
        WasteResponseDTO firstWaste = result.get(0);
        assertEquals(1L, firstWaste.getId());
        assertEquals(1L, firstWaste.getIngredientId());
        assertEquals("Harina", firstWaste.getIngredientName());
        assertEquals(2.0, firstWaste.getQuantity());
        assertEquals("Derrame", firstWaste.getReason());

        WasteResponseDTO secondWaste = result.get(1);
        assertEquals(2L, secondWaste.getId());
        assertEquals(2L, secondWaste.getIngredientId());
        assertEquals("Azúcar", secondWaste.getIngredientName());
        assertEquals(1.0, secondWaste.getQuantity());
        assertEquals("Caducado", secondWaste.getReason());

        verify(wasteRepository, times(1)).findAll();
    }

    @Test
    void getWasteByIngredient_ShouldReturnWasteForSpecificIngredient() {
        // Given
        Long ingredientId = 1L;
        StockEntity ingredient = new StockEntity();
        ingredient.setId(ingredientId);
        ingredient.setName("Harina");

        WasteEntity waste1 = new WasteEntity(ingredient, 2.0, "kg", "Derrame", "Accidente");
        waste1.setId(1L);
        waste1.setTimestamp(LocalDateTime.now());

        WasteEntity waste2 = new WasteEntity(ingredient, 1.0, "kg", "Caducado", "Vencimiento");
        waste2.setId(2L);
        waste2.setTimestamp(LocalDateTime.now());

        when(wasteRepository.findByIngredientId(ingredientId)).thenReturn(List.of(waste1, waste2));

        // When
        List<WasteResponseDTO> result = wasteService.getWasteByIngredient(ingredientId);

        // Then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Harina", result.get(0).getIngredientName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Harina", result.get(1).getIngredientName());

        verify(wasteRepository, times(1)).findByIngredientId(ingredientId);
    }

    @Test
    void deleteWaste_ShouldDeleteWasteAndRevertStock() {
        // Given
        Long wasteId = 1L;
        
        StockEntity ingredient = new StockEntity();
        ingredient.setId(1L);
        ingredient.setName("Harina");
        ingredient.setCurrentStock(15.0);

        WasteEntity waste = new WasteEntity(ingredient, 5.0, "kg", "Caducado", "Producto vencido");
        waste.setId(wasteId);
        waste.setTimestamp(LocalDateTime.now());

        when(wasteRepository.findById(wasteId)).thenReturn(Optional.of(waste));

        // When
        wasteService.deleteWaste(wasteId);

        // Then
        // Verificar que se revirtió el stock (15.0 + 5.0 = 20.0)
        assertEquals(20.0, ingredient.getCurrentStock());
        
        verify(stockRepository, times(1)).save(ingredient);
        verify(wasteRepository, times(1)).delete(waste);
    }

    @Test
    void deleteWaste_WhenWasteNotFound_ShouldThrowException() {
        // Given
        Long wasteId = 999L;
        when(wasteRepository.findById(wasteId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> wasteService.deleteWaste(wasteId));

        assertEquals("Registro de desperdicio no encontrado", exception.getMessage());
        verify(stockRepository, never()).save(any());
        verify(wasteRepository, never()).delete(any());
    }
}