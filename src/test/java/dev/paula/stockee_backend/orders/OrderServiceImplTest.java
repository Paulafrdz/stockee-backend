package dev.paula.stockee_backend.orders;

import dev.paula.stockee_backend.security.CurrentUserService;
import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;
import dev.paula.stockee_backend.user.UserEntity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UserEntity mockUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@test.com");
        return user;
    }

    @Test
    void getRecommendedOrders_WithLowStock_ShouldReturnHighPriority() {
        UserEntity user = mockUser();

        StockEntity lowStock = new StockEntity();
        lowStock.setId(1L);
        lowStock.setName("Flour");
        lowStock.setCurrentStock(5.0);
        lowStock.setMinimumStock(20.0);
        lowStock.setUnit("kg");

        when(currentUserService.get()).thenReturn(user);
        when(stockRepository.findAllByUser(user)).thenReturn(Arrays.asList(lowStock));

        List<OrderResponseDTO> result = orderService.getRecommendedOrders();

        assertEquals(1, result.size());
        assertEquals("high", result.get(0).getPriority());
        assertTrue(result.get(0).getRecommendedQuantity() > 0);
    }

    @Test
    void getRecommendedOrders_WithAdequateStock_ShouldReturnLowPriority() {
        UserEntity user = mockUser();

        StockEntity stock = new StockEntity();
        stock.setId(1L);
        stock.setName("Sugar");
        stock.setCurrentStock(25.0);
        stock.setMinimumStock(20.0);
        stock.setUnit("kg");

        when(currentUserService.get()).thenReturn(user);
        when(stockRepository.findAllByUser(user)).thenReturn(Arrays.asList(stock));

        List<OrderResponseDTO> result = orderService.getRecommendedOrders();

        assertEquals(1, result.size());
        assertEquals("low", result.get(0).getPriority());
        assertEquals(0.0, result.get(0).getRecommendedQuantity());
    }

    @Test
    void createOrder_WithValidItem_ShouldUpdateStock() {
        UserEntity user = mockUser();

        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setId(1L);
        item.setRecommendedQuantity(10.0);
        item.setUnit("kg");

        OrderRequestDTO request = new OrderRequestDTO(Arrays.asList(item));

        StockEntity stock = new StockEntity();
        stock.setId(1L);
        stock.setCurrentStock(5.0);
        stock.setUnit("kg");

        when(currentUserService.get()).thenReturn(user);
        when(stockRepository.findByIdAndUser(eq(1L), eq(user)))
                .thenReturn(Optional.of(stock));
        when(orderRepository.save(any(OrderEntity.class)))
                .thenReturn(new OrderEntity());

        orderService.createOrder(request);

        verify(stockRepository).save(stock);
        assertEquals(15.0, stock.getCurrentStock());
    }

    @Test
    void getOrderHistory_ShouldReturnConvertedDTOs() {
        UserEntity user = mockUser();

        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setOrderDate(LocalDateTime.now());
        order.setItemCount(1);

        Page<OrderEntity> page = new PageImpl<>(List.of(order));

        when(currentUserService.get()).thenReturn(user);
        when(orderRepository.findAllByUserOrderByOrderDateDesc(eq(user), any(PageRequest.class)))
                .thenReturn(page);

        List<OrderHistoryResponseDTO> result = orderService.getOrderHistory(5);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}