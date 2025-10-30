package dev.paula.stockee_backend.analytics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;
import dev.paula.stockee_backend.waste.WasteEntity;
import dev.paula.stockee_backend.waste.WasteRepository;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.sql.init.mode=never",
    "spring.jpa.defer-datasource-initialization=false"
})
@Transactional
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WasteRepository wasteRepository;

    @Autowired
    private StockRepository stockRepository;

    @Test
    @WithMockUser
    void getAnalyticsStats_WithNoWaste_ShouldReturnZeroStats() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/analytics/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWaste").value(0.0))
                .andExpect(jsonPath("$.expiredWaste").value(0.0))
                .andExpect(jsonPath("$.cookingErrorsWaste").value(0.0))
                .andExpect(jsonPath("$.expiredCount").value(0))
                .andExpect(jsonPath("$.cookingErrorsCount").value(0));
    }

    @Test
    @WithMockUser
    void getAnalyticsStats_WithWasteData_ShouldReturnStats() throws Exception {
        // Arrange - Crear datos de prueba
        StockEntity flour = createStock("Flour", "kg", 100.0);
        StockEntity milk = createStock("Milk", "L", 50.0);
        
        createWaste(flour, 5.0, "expired", "Expired flour");
        createWaste(milk, 2.0, "burned", "Burned milk");

        // Act & Assert
        mockMvc.perform(get("/api/analytics/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWaste").exists())
                .andExpect(jsonPath("$.expiredWaste").exists())
                .andExpect(jsonPath("$.cookingErrorsWaste").exists());
    }

    @Test
    void getAnalyticsStats_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/analytics/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getWasteTypes_ShouldReturnCategorizedWaste() throws Exception {
        // Arrange
        StockEntity sugar = createStock("Sugar", "kg", 20.0);
        createWaste(sugar, 2.0, "expired", "Expired sugar");

        // Act & Assert
        mockMvc.perform(get("/api/analytics/waste-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void getWasteTrend_ShouldReturnMonthlyTrend() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/analytics/waste-trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").exists());
    }

    // Helper methods
    private StockEntity createStock(String name, String unit, double currentStock) {
        StockEntity stock = new StockEntity();
        stock.setName(name);
        stock.setUnit(unit);
        stock.setCurrentStock(currentStock);
        stock.setMinimumStock(10.0);
        return stockRepository.save(stock);
    }

    private void createWaste(StockEntity ingredient, Double quantity, String reason, String details) {
        WasteEntity waste = new WasteEntity(ingredient, quantity, ingredient.getUnit(), reason, details);
        wasteRepository.save(waste);
    }
}