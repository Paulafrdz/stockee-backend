package dev.paula.stockee_backend.orders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void getRecommendedOrders_WithLowStock_ShouldReturnHighPriority() {
        // Arrange
        StockEntity lowStock = new StockEntity();
        lowStock.setId(1L);
        lowStock.setName("Flour");
        lowStock.setCurrentStock(5.0);
        lowStock.setMinimumStock(20.0);
        lowStock.setUnit("kg");

        when(stockRepository.findAll()).thenReturn(Arrays.asList(lowStock));

        // Act
        List<OrderResponseDTO> result = orderService.getRecommendedOrders();

        // Assert
        assertEquals(1, result.size());
        OrderResponseDTO recommendation = result.get(0);
        assertEquals("high", recommendation.getPriority());
        assertTrue(recommendation.getRecommendedQuantity() > 0);
    }

    @Test
    void getRecommendedOrders_WithAdequateStock_ShouldReturnLowPriority() {
        // Arrange
        StockEntity adequateStock = new StockEntity();
        adequateStock.setId(1L);
        adequateStock.setName("Sugar");
        adequateStock.setCurrentStock(25.0);
        adequateStock.setMinimumStock(20.0);
        adequateStock.setUnit("kg");

        when(stockRepository.findAll()).thenReturn(Arrays.asList(adequateStock));

        // Act
        List<OrderResponseDTO> result = orderService.getRecommendedOrders();

        // Assert
        assertEquals(1, result.size());
        OrderResponseDTO recommendation = result.get(0);
        assertEquals("low", recommendation.getPriority());
        assertEquals(0.0, recommendation.getRecommendedQuantity());
    }

    @Test
    void createOrder_WithValidItem_ShouldUpdateStock() {
        // Arrange
        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setId(1L);
        item.setRecommendedQuantity(10.0);
        item.setUnit("kg");

        OrderRequestDTO request = new OrderRequestDTO(Arrays.asList(item));

        StockEntity stock = new StockEntity();
        stock.setId(1L);
        stock.setName("Flour");
        stock.setCurrentStock(5.0);
        stock.setMinimumStock(20.0);
        stock.setUnit("kg");

        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(new OrderEntity());

        // Act
        orderService.createOrder(request);

        // Assert
        verify(stockRepository).save(stock);
        assertEquals(15.0, stock.getCurrentStock()); // 5.0 + 10.0
    }

    @Test
    void getOrderHistory_ShouldReturnConvertedDTOs() {
        // Arrange
        int limit = 5;
        
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setOrderDate(LocalDateTime.now());
        order.setItemCount(1);

        Page<OrderEntity> orderPage = new PageImpl<>(Arrays.asList(order));
        when(orderRepository.findAllByOrderByOrderDateDesc(any(PageRequest.class)))
            .thenReturn(orderPage);

        // Act
        List<OrderHistoryResponseDTO> result = orderService.getOrderHistory(limit);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}