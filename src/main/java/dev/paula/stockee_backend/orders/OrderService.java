package dev.paula.stockee_backend.orders;

import java.util.List;

public interface OrderService {
    List<OrderResponseDTO> getAllOrders();
    OrderResponseDTO createOrder(OrderRequestDTO request);
    OrderResponseDTO getOrderById(Long id);
    void deleteOrder(Long id);
}

