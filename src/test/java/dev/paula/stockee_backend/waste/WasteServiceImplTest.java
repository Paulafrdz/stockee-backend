package dev.paula.stockee_backend.waste;

import dev.paula.stockee_backend.security.CurrentUserService;
import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;
import dev.paula.stockee_backend.user.UserEntity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WasteServiceImplTest {

    @Mock
    private WasteRepository wasteRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private WasteServiceImpl wasteService;

    private UserEntity mockUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@test.com");
        return user;
    }

    @Test
    void registerWaste_ShouldRegisterWasteAndUpdateStock() {
        UserEntity user = mockUser();

        WasteRequestDTO request = new WasteRequestDTO();
        request.setIngredientId(1L);
        request.setQuantity(5.0);
        request.setUnit("kg");
        request.setReason("Caducado");
        request.setDetails("Producto vencido");

        StockEntity ingredient = new StockEntity();
        ingredient.setId(1L);
        ingredient.setCurrentStock(20.0);

        WasteEntity wasteEntity = new WasteEntity(
                ingredient, 5.0, "kg", "Caducado", "Producto vencido"
        );
        wasteEntity.setId(1L);
        wasteEntity.setTimestamp(LocalDateTime.now());

        when(currentUserService.get()).thenReturn(user);
        when(stockRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(ingredient));
        when(wasteRepository.save(any(WasteEntity.class)))
                .thenReturn(wasteEntity);

        WasteResponseDTO result = wasteService.registerWaste(request);

        assertEquals(15.0, ingredient.getCurrentStock());
        verify(stockRepository).save(ingredient);
        verify(wasteRepository).save(any(WasteEntity.class));
    }

    @Test
    void registerWaste_WhenIngredientNotFound_ShouldThrowException() {
        UserEntity user = mockUser();

        WasteRequestDTO request = new WasteRequestDTO();
        request.setIngredientId(999L);
        request.setQuantity(5.0);

        when(currentUserService.get()).thenReturn(user);
        when(stockRepository.findByIdAndUser(999L, user))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> wasteService.registerWaste(request));

        assertEquals("Ingrediente no encontrado", ex.getMessage());
        verify(wasteRepository, never()).save(any());
    }

    @Test
    void deleteWaste_ShouldDeleteAndRevertStock() {
        UserEntity user = mockUser();

        StockEntity ingredient = new StockEntity();
        ingredient.setId(1L);
        ingredient.setCurrentStock(15.0);

        WasteEntity waste = new WasteEntity(ingredient, 5.0, "kg", "Caducado", "x");
        waste.setId(1L);

        when(currentUserService.get()).thenReturn(user);
        when(wasteRepository.findById(1L)).thenReturn(Optional.of(waste));

        wasteService.deleteWaste(1L);

        assertEquals(20.0, ingredient.getCurrentStock());
        verify(stockRepository).save(ingredient);
        verify(wasteRepository).delete(waste);
    }
}