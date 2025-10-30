package dev.paula.stockee_backend.orders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.paula.stockee_backend.implementations.IOrderService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private IOrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void getRecommendedOrders_ShouldReturnListOfOrders() {
        // Arrange
        List<OrderResponseDTO> expectedOrders = Arrays.asList(
            OrderResponseDTO.builder()
                .id(1L)
                .name("Product A")
                .currentStock(10.0)
                .minimumStock(20.0)
                .recommendedQuantity(15.0)
                .weeklyUsage(5.0)
                .unit("kg")
                .priority("high")
                .lastOrderDate(LocalDateTime.now())
                .build(),
            OrderResponseDTO.builder()
                .id(2L)
                .name("Product B")
                .currentStock(25.0)
                .minimumStock(15.0)
                .recommendedQuantity(0.0)
                .weeklyUsage(3.0)
                .unit("units")
                .priority("low")
                .lastOrderDate(null)
                .build()
        );
        when(orderService.getRecommendedOrders()).thenReturn(expectedOrders);

        // Act
        List<OrderResponseDTO> result = orderController.getRecommendedOrders();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product A", result.get(0).getName());
        assertEquals("high", result.get(0).getPriority());
        verify(orderService, times(1)).getRecommendedOrders();
    }

    @Test
    void createOrder_ShouldCallServiceWithCorrectParameters() {
        // Arrange
        OrderItemRequestDTO item1 = new OrderItemRequestDTO();
        item1.setId(1L);
        item1.setRecommendedQuantity(10.0);
        item1.setUnit("kg");
        
        OrderItemRequestDTO item2 = new OrderItemRequestDTO();
        item2.setId(2L);
        item2.setRecommendedQuantity(5.0);
        item2.setUnit("units");
        
        OrderRequestDTO request = new OrderRequestDTO(Arrays.asList(item1, item2));

        // Act
        orderController.createOrder(request);

        // Assert
        verify(orderService, times(1)).createOrder(request);
    }

    @Test
    void getOrderHistory_WithDefaultLimit_ShouldCallServiceWithDefaultLimit() {
        // Arrange
        OrderHistoryResponseDTO history = new OrderHistoryResponseDTO();
        history.setId(1L);
        history.setOrderDate(LocalDateTime.now());
        history.setItemCount(2);
        history.setItems(Arrays.asList());
        
        when(orderService.getOrderHistory(50)).thenReturn(Arrays.asList(history));

        // Act
        List<OrderHistoryResponseDTO> result = orderController.getOrderHistory(50);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getItemCount());
        verify(orderService, times(1)).getOrderHistory(50);
    }

    @Test
    void getOrderHistory_WithCustomLimit_ShouldCallServiceWithCustomLimit() {
        // Arrange
        int customLimit = 10;
        OrderHistoryResponseDTO history = new OrderHistoryResponseDTO();
        history.setId(1L);
        history.setOrderDate(LocalDateTime.now());
        history.setItemCount(1);
        history.setItems(Arrays.asList());
        
        when(orderService.getOrderHistory(customLimit)).thenReturn(Arrays.asList(history));

        // Act
        List<OrderHistoryResponseDTO> result = orderController.getOrderHistory(customLimit);

        // Assert
        assertNotNull(result);
        verify(orderService, times(1)).getOrderHistory(customLimit);
    }
}