package dev.paula.stockee_backend.dish;

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
class DishServiceImplTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private DishServiceImpl dishService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("test");
        user.setEmail("test@test.com");

        when(currentUserService.get()).thenReturn(user);
    }

    @Test
    void createDish_shouldCreateDishWithIngredients() {

        // STOCK
        StockEntity flour = new StockEntity();
        flour.setId(1L);
        flour.setName("Flour");

        when(stockRepository.findById(1L)).thenReturn(Optional.of(flour));
        when(dishRepository.save(any())).thenAnswer(i -> {
            DishEntity d = i.getArgument(0);
            d.setId(10L);
            return d;
        });

        // DTO request
        DishRequestDTO request = new DishRequestDTO();
        request.setName("Pizza");
        request.setDescription("Nice pizza");
        request.setIcon("🍕");

        DishRequestDTO.DishIngredientDTO ing = new DishRequestDTO.DishIngredientDTO();
        ing.setIngredientId(1L);
        ing.setQuantity(2.0);
        ing.setUnit("kg");

        request.setIngredients(List.of(ing));

        // EXECUTE
        DishResponseDTO result = dishService.createDish(request);

        // VERIFY
        assertNotNull(result);
        assertEquals("Pizza", result.getName());

        verify(stockRepository).findById(1L);
        verify(dishRepository).save(any(DishEntity.class));
    }

    @Test
    void getAllDishes_shouldReturnList() {

        DishEntity dish = new DishEntity();
        dish.setId(1L);
        dish.setName("Pizza");
        dish.setUser(user);
        dish.setIngredients(List.of());

        when(dishRepository.findAllByUser(user))
                .thenReturn(List.of(dish));

        List<DishResponseDTO> result = dishService.getAllDishes();

        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getName());

        verify(dishRepository).findAllByUser(user);
    }

    @Test
    void deleteDish_shouldDeleteDish() {

        DishEntity dish = new DishEntity();
        dish.setId(1L);
        dish.setUser(user);

        when(dishRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(dish));

        dishService.deleteDish(1L);

        verify(dishRepository).delete(dish);
    }

    @Test
    void updateDish_shouldUpdateDish() {

        DishEntity dish = new DishEntity();
        dish.setId(1L);
        dish.setUser(user);
        dish.setIngredients(new java.util.ArrayList<>());

        when(dishRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(dish));

        when(stockRepository.findById(1L))
                .thenReturn(Optional.of(new StockEntity()));

        when(dishRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        DishRequestDTO request = new DishRequestDTO();
        request.setName("Updated");
        request.setDescription("Updated desc");
        request.setIcon("🔥");

        DishRequestDTO.DishIngredientDTO ing = new DishRequestDTO.DishIngredientDTO();
        ing.setIngredientId(1L);
        ing.setQuantity(1.0);
        ing.setUnit("kg");

        request.setIngredients(List.of(ing));

        DishResponseDTO result = dishService.updateDish(1L, request);

        assertNotNull(result);
        assertEquals("Updated", result.getName());

        verify(dishRepository).save(any(DishEntity.class));
    }
}