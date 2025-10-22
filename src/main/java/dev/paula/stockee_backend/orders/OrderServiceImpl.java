package dev.paula.stockee_backend.orders;

import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StockRepository ingredientRepository;

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        StockEntity ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado"));

        // Calcula el estado automáticamente según los stocks
        String status = calculateStatus(request.getCurrentStock(), request.getMinimumStock());

        OrderEntity order = OrderEntity.builder()
                .ingredient(ingredient)
                .currentStock(request.getCurrentStock())
                .minimumStock(request.getMinimumStock())
                .weeklyUsage(request.getWeeklyUsage())
                .recommendedQuantity(request.getRecommendedQuantity())
                .status(status)
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .build();

        orderRepository.save(order);
        return mapToResponseDTO(order);
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        return mapToResponseDTO(order);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("El pedido no existe");
        }
        orderRepository.deleteById(id);
    }

    private OrderResponseDTO mapToResponseDTO(OrderEntity order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .ingredientName(order.getIngredient().getName())
                .currentStock(order.getCurrentStock())
                .minimumStock(order.getMinimumStock())
                .weeklyUsage(order.getWeeklyUsage())
                .recommendedQuantity(order.getRecommendedQuantity())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private String calculateStatus(Double current, Double minimum) {
        if (current <= 0) return "CRITICAL";
        if (current < minimum) return "LOW";
        return "OK";
    }
}
