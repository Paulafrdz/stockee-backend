package dev.paula.stockee_backend.orders;

import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.user.UserEntity;
import dev.paula.stockee_backend.stock.StockRepository;
import dev.paula.stockee_backend.lotes.*;
import dev.paula.stockee_backend.security.CurrentUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final LoteRepository loteRepository;
    private final CurrentUserService currentUserService;

    @Override
    public List<OrderResponseDTO> getRecommendedOrders() {
        return getRecommendations();
    }

    // Gets order recommendations based on current stock
    public List<OrderResponseDTO> getRecommendations() {
        List<StockEntity> allStock = stockRepository.findAllByUser(currentUserService.get());

        return allStock.stream()
                .map(this::calculateRecommendation)
                .collect(Collectors.toList());
    }

    // Calculates the recommendation for a stock item
    private OrderResponseDTO calculateRecommendation(StockEntity stock) {
        OrderResponseDTO recommendation = new OrderResponseDTO();

        recommendation.setId(stock.getId());
        recommendation.setName(stock.getName());
        recommendation.setCurrentStock(stock.getCurrentStock());
        recommendation.setMinimumStock(stock.getMinimumStock());
        recommendation.setUnit(stock.getUnit());
        recommendation.setLastOrderDate(null);

        // Calculate weekly usage (based on minimum stock)
        double weeklyUsage = stock.getMinimumStock() > 0
                ? stock.getMinimumStock()
                : 5.0;
        recommendation.setWeeklyUsage(weeklyUsage);

        // Calculate recommended quantity
        double recommendedQty = calculateRecommendedQuantity(
                stock.getCurrentStock(),
                stock.getMinimumStock(),
                weeklyUsage);
        recommendation.setRecommendedQuantity(recommendedQty);

        // Determine priority
        String priority = determinePriority(stock.getCurrentStock(), stock.getMinimumStock());
        recommendation.setPriority(priority);

        return recommendation;
    }

    // Calculates the recommended quantity

    private double calculateRecommendedQuantity(double currentStock, double minimumStock, double weeklyUsage) {
        if (currentStock >= minimumStock * 1.2) {
            return 0.0;
        }

        if (currentStock < minimumStock) {
            double shortage = minimumStock - currentStock;
            double weeklySupply = weeklyUsage * 2;
            return Math.round((shortage + weeklySupply) * 10.0) / 10.0;
        }

        double threshold = minimumStock * 1.2;
        if (currentStock < threshold) {
            return Math.round((weeklyUsage * 1.5) * 10.0) / 10.0;
        }

        return 0.0;
    }

    // Determines the priority
    private String determinePriority(double currentStock, double minimumStock) {
        if (minimumStock == 0) {
            return "low";
        }

        double ratio = currentStock / minimumStock;

        if (ratio <= 0.5) {
            return "high";
        } else if (ratio < 1.0) {
            return "medium";
        } else {
            return "low";
        }
    }

    // Creates a new order and updates the stock

    @Transactional
    public void createOrder(OrderRequestDTO createOrderRequest) {
        // Create the order
        UserEntity user = currentUserService.get();

        OrderEntity order = new OrderEntity();
        order.setOrderDate(LocalDateTime.now());
        order.setUser(user);

        // Process each item
        for (OrderItemRequestDTO itemRequest : createOrderRequest.getItems()) {
            StockEntity stock = stockRepository
                    .findByIdAndUser(itemRequest.getId(), user)
                    .orElseThrow(() -> new RuntimeException("Stock no encontrado"));
            // Create order item
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setStock(stock);
            orderItem.setQuantity(java.math.BigDecimal.valueOf(itemRequest.getRecommendedQuantity()));
            orderItem.setUnit(itemRequest.getUnit());

            order.addItem(orderItem);

            // ✅ UPDATE STOCK: Add the ordered quantity
            double newStock = stock.getCurrentStock() + itemRequest.getRecommendedQuantity();
            stock.setCurrentStock(newStock);
            stockRepository.save(stock);

            if (stock.getShelfLifeDays() != null) {

                LocalDate orderDate = LocalDate.now();
                LoteEntity lote = LoteEntity.builder()
                        .stock(stock)
                        .quantity(itemRequest.getRecommendedQuantity())
                        .unit(itemRequest.getUnit())
                        .orderDate(orderDate)
                        .expiryDate(orderDate.plusDays(stock.getShelfLifeDays()))
                        .build();
                loteRepository.save(lote);

            }
        }

        order.updateItemCount();

        orderRepository.save(order);
    }

    // Gets order history (read-only)

    public List<OrderHistoryResponseDTO> getOrderHistory(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);

        return orderRepository.findAllByUserOrderByOrderDateDesc(currentUserService.get(), pageRequest)
                .stream()
                .map(this::convertToHistoryDTO)
                .collect(Collectors.toList());
    }

    // Converts Order to History DTO
    private OrderHistoryResponseDTO convertToHistoryDTO(OrderEntity order) {
        OrderHistoryResponseDTO dto = new OrderHistoryResponseDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setItemCount(order.getItemCount());

        List<OrderItemHistoryResponseDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    OrderItemHistoryResponseDTO itemDto = new OrderItemHistoryResponseDTO();
                    itemDto.setName(item.getStock().getName());
                    itemDto.setQuantity(item.getQuantity() != null ? item.getQuantity().doubleValue() : null);
                    itemDto.setUnit(item.getUnit());
                    return itemDto;
                })
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }
}