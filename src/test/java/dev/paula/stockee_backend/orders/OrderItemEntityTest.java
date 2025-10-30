package dev.paula.stockee_backend.orders;

import dev.paula.stockee_backend.stock.StockEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemEntityTest {

    @Test
    void addItem_ShouldSetOrderReference() {
        OrderEntity order = new OrderEntity();
        OrderItemEntity item = new OrderItemEntity();
        
        order.addItem(item);
        
        assertEquals(1, order.getItems().size());
        assertEquals(order, item.getOrder());
    }

    @Test
    void addItem_WithStock_ShouldSetQuantityAndUnit() {
        OrderEntity order = new OrderEntity();
        StockEntity stock = new StockEntity(); 
        OrderItemEntity item = new OrderItemEntity();
        item.setStock(stock);
        item.setQuantity(BigDecimal.valueOf(5.5));
        item.setUnit("kg");

        order.addItem(item);

        assertEquals(1, order.getItems().size());
        assertEquals(order, item.getOrder());
        assertEquals(stock, item.getStock());
        assertEquals(BigDecimal.valueOf(5.5), item.getQuantity());
        assertEquals("kg", item.getUnit());
    }

    @Test
    void updateItemCount_ShouldSetCorrectItemCount() {
        OrderEntity order = new OrderEntity();
        order.addItem(new OrderItemEntity());
        order.addItem(new OrderItemEntity());

        order.updateItemCount();

        assertEquals(2, order.getItemCount());
    }

    @Test
    void prePersist_ShouldSetOrderDateIfNull() {
        OrderEntity order = new OrderEntity();
        assertNull(order.getOrderDate());

        // Simula persistencia
        order.onCreate();

        assertNotNull(order.getOrderDate());
        assertTrue(order.getOrderDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void prePersist_ShouldNotOverrideExistingOrderDate() {
        LocalDateTime date = LocalDateTime.of(2025, 10, 29, 12, 0);
        OrderEntity order = new OrderEntity();
        order.setOrderDate(date);

        order.onCreate();

        assertEquals(date, order.getOrderDate());
    }
}
