package dev.paula.stockee_backend.implementations;

import java.util.List;

import dev.paula.stockee_backend.orders.OrderHistoryResponseDTO;
import dev.paula.stockee_backend.orders.OrderRequestDTO;
import dev.paula.stockee_backend.orders.OrderResponseDTO;

public interface IOrderService {
    List<OrderResponseDTO> getRecommendedOrders();
    void createOrder(OrderRequestDTO orderRequest);
    List<OrderHistoryResponseDTO> getOrderHistory(int limit);
}

