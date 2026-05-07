package dev.paula.stockee_backend.sales;

import dev.paula.stockee_backend.dish.*;
import dev.paula.stockee_backend.stock.*;
import dev.paula.stockee_backend.security.CurrentUserService;
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
class SaleServiceImplTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private SaleServiceImpl saleService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("test");
        user.setEmail("test@test.com");
    }

    @Test
    void createSale_shouldCreateSaleAndReduceStock() {
        when(currentUserService.get()).thenReturn(user);

        StockEntity flour = new StockEntity();
        flour.setId(1L);
        flour.setCurrentStock(100);

        DishIngredientEntity ingredient = new DishIngredientEntity();
        ingredient.setIngredient(flour);
        ingredient.setQuantity(2.0);

        DishEntity dish = new DishEntity();
        dish.setId(10L);
        dish.setName("Pizza");
        dish.setIngredients(List.of(ingredient));

        when(dishRepository.findById(10L)).thenReturn(Optional.of(dish));
        when(stockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SaleRequestDTO.SaleLineDTO line = new SaleRequestDTO.SaleLineDTO();
        line.setDishId(10L);
        line.setQuantity(3);

        SaleRequestDTO request = new SaleRequestDTO();
        request.setLines(List.of(line));

        SaleEntity savedSale = new SaleEntity();
        savedSale.setId(99L);
        savedSale.setLines(List.of());

        when(saleRepository.save(any())).thenReturn(savedSale);

        SaleResponseDTO result = saleService.createSale(request);

        assertNotNull(result);
        verify(stockRepository, atLeastOnce()).save(any());
        verify(saleRepository).save(any());

        assertEquals(94, flour.getCurrentStock());
        
    }

    @Test
    void createSale_shouldThrowException_whenEmptyLines() {

        SaleRequestDTO request = new SaleRequestDTO();
        request.setLines(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> saleService.createSale(request));

        assertEquals("La venta debe tener al menos un plato", ex.getMessage());
    }

    @Test
    void getAllSales_shouldReturnList() {
        when(currentUserService.get()).thenReturn(user);
        SaleEntity sale = new SaleEntity();
        sale.setId(1L);
        sale.setLines(List.of());

        // 👇 IMPORTANTE: usar user del service
        when(saleRepository.findAllByUser(user)).thenReturn(List.of(sale));

        List<SaleResponseDTO> result = saleService.getAllSales();

        assertEquals(1, result.size());
        verify(saleRepository).findAllByUser(user);
    }

    @Test
    void deleteSale_shouldRestoreStock() {
        when(currentUserService.get()).thenReturn(user);
        StockEntity stock = new StockEntity();
        stock.setCurrentStock(50);

        DishIngredientEntity ingredient = new DishIngredientEntity();
        ingredient.setIngredient(stock);
        ingredient.setQuantity(2.0);

        DishEntity dish = new DishEntity();
        dish.setIngredients(List.of(ingredient));

        SaleLineEntity line = new SaleLineEntity();
        line.setDish(dish);
        line.setQuantity(3);

        SaleEntity sale = new SaleEntity();
        sale.setLines(List.of(line));

        when(saleRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(sale));

        when(stockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        saleService.deleteSale(1L);

        assertEquals(56, stock.getCurrentStock());
        verify(saleRepository).deleteById(1L);
    }
}