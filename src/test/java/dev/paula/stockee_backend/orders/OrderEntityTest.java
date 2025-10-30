package dev.paula.stockee_backend.orders;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityTest {

    @Test
    void addItem_ShouldSetOrderReference() {
        OrderEntity order = new OrderEntity();
        OrderItemEntity item = new OrderItemEntity();
        
        order.addItem(item);
        
        assertEquals(1, order.getItems().size());
        assertEquals(order, item.getOrder());
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
