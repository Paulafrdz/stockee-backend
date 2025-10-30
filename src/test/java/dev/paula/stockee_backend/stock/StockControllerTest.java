package dev.paula.stockee_backend.stock;

import dev.paula.stockee_backend.implementations.IStockService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class StockControllerTest {

    @Mock
    private IStockService stockService;

    @InjectMocks
    private StockController stockController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stockController).build();
    }

    @Test
    void testGetAll() throws Exception {
        StockEntity item = new StockEntity();
        item.setId(1L);
        item.setName("Sugar");

        when(stockService.getAll()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sugar"));
    }

    @Test
    void testAddItem() throws Exception {
        StockEntity item = new StockEntity();
        item.setId(1L);
        item.setName("Flour");

        when(stockService.addItem(any(StockEntity.class))).thenReturn(item);

        mockMvc.perform(post("/api/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Flour\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Flour"));
    }

    @Test
    void testUpdateItem() throws Exception {
        StockEntity updated = new StockEntity();
        updated.setId(1L);
        updated.setName("Salt");

        when(stockService.updateItem(eq(1L), any(StockEntity.class))).thenReturn(updated);

        mockMvc.perform(put("/api/stock/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Salt\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Salt"));
    }

    @Test
    void testUpdateStock() throws Exception {
        StockEntity updated = new StockEntity();
        updated.setId(1L);
        updated.setName("Oil");
        updated.setCurrentStock(15.5);

        when(stockService.updateStock(1L, 15.5)).thenReturn(updated);

        mockMvc.perform(put("/api/stock/1/stock")
                        .param("newStock", "15.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStock").value(15.5));
    }

    @Test
    void testDeleteItem() throws Exception {
        doNothing().when(stockService).deleteItem(1L);

        mockMvc.perform(delete("/api/stock/1"))
                .andExpect(status().isOk());

        verify(stockService, times(1)).deleteItem(1L);
    }
}
