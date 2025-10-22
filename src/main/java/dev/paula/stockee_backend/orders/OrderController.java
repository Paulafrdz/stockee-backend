package dev.paula.stockee_backend.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public OrderResponseDTO createOrder(@RequestBody OrderRequestDTO request) {
        return orderService.createOrder(request);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
