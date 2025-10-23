package dev.paula.stockee_backend.orders;

import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    /**
     * Implementación del método requerido por IOrderService.
     * Retorna las recomendaciones de pedido.
     */
    @Override
    public List<OrderResponseDTO> getRecommendedOrders() {
        return getRecommendations();
    }

    /**
     * Obtiene recomendaciones de pedido basadas en el stock actual
     */
    public List<OrderResponseDTO> getRecommendations() {
        List<StockEntity> allStock = stockRepository.findAll();

        return allStock.stream()
            .map(this::calculateRecommendation)
            .collect(Collectors.toList());
    }
    
    /**
     * Calcula la recomendación para un item de stock
     */
    private OrderResponseDTO calculateRecommendation(StockEntity stock) {
        OrderResponseDTO recommendation = new OrderResponseDTO();
        
        recommendation.setId(stock.getId());
        recommendation.setName(stock.getName());
        recommendation.setCurrentStock(stock.getCurrentStock());
        recommendation.setMinimumStock(stock.getMinimumStock());
        recommendation.setUnit(stock.getUnit());
        recommendation.setLastOrderDate(null); // Puedes cambiarlo si guardas fechas en Stock
        
        // Calcular uso semanal (basado en stock mínimo)
        double weeklyUsage = stock.getMinimumStock() > 0
            ? stock.getMinimumStock()
            : 5.0;
        recommendation.setWeeklyUsage(weeklyUsage);
        
        // Calcular cantidad recomendada
        double recommendedQty = calculateRecommendedQuantity(
            stock.getCurrentStock(),
            stock.getMinimumStock(),
            weeklyUsage
        );
        recommendation.setRecommendedQuantity(recommendedQty);
        
        // Determinar prioridad
        String priority = determinePriority(stock.getCurrentStock(), stock.getMinimumStock());
        recommendation.setPriority(priority);
        
        return recommendation;
    }
    
    /**
     * Calcula la cantidad recomendada
     */
    private double calculateRecommendedQuantity(double currentStock, double minimumStock, double weeklyUsage) {
        if (currentStock < minimumStock) {
            double shortage = minimumStock - currentStock;
            double weeklySupply = weeklyUsage * 2;
            return Math.round((shortage + weeklySupply) * 10.0) / 10.0; // redondea a 1 decimal
        }
        
        double threshold = minimumStock * 1.2;
        if (currentStock < threshold) {
            return Math.round((weeklyUsage * 1.5) * 10.0) / 10.0;
        }
        
        return 0.0;
    }
    
    /**
     * Determina la prioridad
     */
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
    
    /**
     * Crea un nuevo pedido y actualiza el stock
     */
    @Transactional
    public void createOrder(OrderRequestDTO createOrderRequest) {
        // Crear el pedido
        OrderEntity order = new OrderEntity();
        order.setOrderDate(LocalDateTime.now());
        
        // Procesar cada item
        for (OrderItemRequestDTO itemRequest : createOrderRequest.getItems()) {
            StockEntity stock = stockRepository.findById(itemRequest.getId())
                .orElseThrow(() -> new RuntimeException("Stock not found: " + itemRequest.getId()));
            
            // Crear item del pedido
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setStock(stock);
            orderItem.setQuantity(java.math.BigDecimal.valueOf(itemRequest.getRecommendedQuantity()));
            orderItem.setUnit(itemRequest.getUnit());
            
            order.addItem(orderItem);
            
            // ✅ ACTUALIZAR STOCK: Sumar la cantidad pedida
            double newStock = stock.getCurrentStock() + itemRequest.getRecommendedQuantity();
            stock.setCurrentStock(newStock);
            stockRepository.save(stock);
        }
        
        // Actualizar item count
        order.updateItemCount();
        
        // Guardar pedido
        orderRepository.save(order);
    }
    
    /**
     * Obtiene el historial de pedidos (solo para ver)
     */
    public List<OrderHistoryResponseDTO> getOrderHistory(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        
        return orderRepository.findAllByOrderByOrderDateDesc(pageRequest)
            .stream()
            .map(this::convertToHistoryDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Convierte Order a DTO de historial
     */
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
