package dev.paula.stockee_backend.waste;

import dev.paula.stockee_backend.security.CurrentUserService;
import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;
import dev.paula.stockee_backend.user.UserEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private WasteServiceImpl wasteService;

    private UserEntity user;
    private StockEntity ingredient;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@test.com");

        ingredient = new StockEntity();
        ingredient.setId(1L);
        ingredient.setName("Tomate");
        ingredient.setCurrentStock(20.0);
    }

    @Test
    void registerWaste_ShouldRegisterWasteAndUpdateStock() {

        WasteRequestDTO request = new WasteRequestDTO();
        request.setIngredientId(1L);
        request.setQuantity(5.0);
        request.setUnit("kg");
        request.setReason("Caducado");
        request.setDetails("Producto vencido");

        WasteEntity savedWaste = new WasteEntity(
                ingredient,
                5.0,
                "kg",
                "Caducado",
                "Producto vencido"
        );

        savedWaste.setId(1L);

        when(currentUserService.get()).thenReturn(user);

        when(stockRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(ingredient));

        when(wasteRepository.save(any(WasteEntity.class)))
                .thenReturn(savedWaste);

        WasteResponseDTO result = wasteService.registerWaste(request);

        assertNotNull(result);

        assertEquals(15.0, ingredient.getCurrentStock());

        verify(stockRepository).save(ingredient);
        verify(wasteRepository).save(any(WasteEntity.class));
    }

    @Test
    void getAllWaste_ShouldReturnWasteList() {

        WasteEntity waste = new WasteEntity(
                ingredient,
                5.0,
                "kg",
                "Caducado",
                "Producto vencido"
        );

        waste.setId(1L);

        when(currentUserService.get()).thenReturn(user);

        when(wasteRepository.findAllByUser(user))
                .thenReturn(List.of(waste));

        List<WasteResponseDTO> result = wasteService.getAllWaste();

        assertEquals(1, result.size());
        assertEquals("Tomate", result.get(0).getIngredientName());
    }

    @Test
    void deleteWaste_ShouldDeleteWasteAndRestoreStock() {

        ingredient.setCurrentStock(10.0);

        WasteEntity waste = new WasteEntity(
                ingredient,
                5.0,
                "kg",
                "Caducado",
                "Producto vencido"
        );

        waste.setId(1L);

        when(wasteRepository.findById(1L))
                .thenReturn(Optional.of(waste));

        wasteService.deleteWaste(1L);

        assertEquals(15.0, ingredient.getCurrentStock());

        verify(stockRepository).save(ingredient);
        verify(wasteRepository).delete(waste);
    }
}