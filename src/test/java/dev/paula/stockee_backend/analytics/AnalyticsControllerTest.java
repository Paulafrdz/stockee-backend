package dev.paula.stockee_backend.analytics;

import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;
import dev.paula.stockee_backend.user.UserEntity;
import dev.paula.stockee_backend.user.UserRepository;
import dev.paula.stockee_backend.waste.WasteEntity;
import dev.paula.stockee_backend.waste.WasteRepository;
import dev.paula.stockee_backend.security.CurrentUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WasteRepository wasteRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private CurrentUserService currentUserService;

    private UserEntity mockUser;

    @BeforeEach
    void setup() {

        mockUser = new UserEntity();
        mockUser.setUsername("testuser");   
        mockUser.setEmail("test@test.com");
        mockUser.setPassword(passwordEncoder.encode("test123"));

        mockUser = userRepository.save(mockUser);

        when(currentUserService.get()).thenReturn(mockUser);
    }


    @Test
    @WithMockUser
    void getAnalyticsStats_WithNoWaste_ShouldReturnZeroStats() throws Exception {

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

        StockEntity flour = createStock("Flour", "kg", 100.0);
        StockEntity milk = createStock("Milk", "L", 50.0);

        createWaste(flour, 5.0, "expired", "Expired flour");
        createWaste(milk, 2.0, "burned", "Burned milk");

        mockMvc.perform(get("/api/analytics/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWaste").exists())
                .andExpect(jsonPath("$.expiredWaste").exists())
                .andExpect(jsonPath("$.cookingErrorsWaste").exists());
    }

    @Test
    void getAnalyticsStats_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {

        mockMvc.perform(get("/api/analytics/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getWasteTypes_ShouldReturnCategorizedWaste() throws Exception {

        StockEntity sugar = createStock("Sugar", "kg", 20.0);
        createWaste(sugar, 2.0, "expired", "Expired sugar");

        mockMvc.perform(get("/api/analytics/waste-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void getWasteTrend_ShouldReturnMonthlyTrend() throws Exception {

        mockMvc.perform(get("/api/analytics/waste-trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").exists());
    }

    // ================= HELPERS =================

    private StockEntity createStock(String name, String unit, double currentStock) {

        StockEntity stock = new StockEntity();
        stock.setName(name);
        stock.setUnit(unit);
        stock.setCurrentStock(currentStock);
        stock.setMinimumStock(10.0);
        stock.setUser(mockUser); 
        return stockRepository.save(stock);
    }

    private void createWaste(StockEntity ingredient, Double quantity, String reason, String details) {

        WasteEntity waste = new WasteEntity(
                ingredient,
                quantity,
                ingredient.getUnit(),
                reason,
                details
        );

        waste.setUser(mockUser); 
        wasteRepository.save(waste);
    }
}