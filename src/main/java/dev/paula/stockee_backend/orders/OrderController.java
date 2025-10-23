package dev.paula.stockee_backend.orders;

import org.springframework.web.bind.annotation.*;

import dev.paula.stockee_backend.implementations.IOrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    // GET /api/order - for recommendations
    @GetMapping
    public List<OrderResponseDTO> getRecommendedOrders() {
        return orderService.getRecommendedOrders();
    }

    // POST /api/order - for creating orders
    @PostMapping
    public void createOrder(@RequestBody OrderRequestDTO orderRequest) {
        orderService.createOrder(orderRequest);
    }

    // GET /api/order/history - for order history
    @GetMapping("/history")
    public List<OrderHistoryResponseDTO> getOrderHistory(
            @RequestParam(defaultValue = "50") int limit) {
        return orderService.getOrderHistory(limit);
    }
}
