package dev.paula.stockee_backend.implementations;

import java.util.List;

import dev.paula.stockee_backend.orders.OrderRequestDTO;
import dev.paula.stockee_backend.orders.OrderResponseDTO;

public interface IOrderService {

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO createOrder(OrderRequestDTO request);

    OrderResponseDTO getOrderById(Long id);

    void deleteOrder(Long id);
}

