package dev.paula.stockee_backend.waste;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WasteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WasteService wasteService;

    @InjectMocks
    private WasteController wasteController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private WasteRequestDTO request;
    private WasteResponseDTO response;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(wasteController).build();

        request = new WasteRequestDTO();
        request.setIngredientId(1L);
        request.setQuantity(2.5);
        request.setUnit("kg");
        request.setReason("Expired");
        request.setDetails("Expired in storage");

        response = new WasteResponseDTO(
            1L, 1L, "Harina", 2.5, "kg", "Expired", "Expired in storage", LocalDateTime.now()
        );
    }

    @Test
    void registerWaste_ShouldReturnDTO() throws Exception {
        when(wasteService.registerWaste(any(WasteRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/waste")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ingredientId").value(1L))
                .andExpect(jsonPath("$.ingredientName").value("Harina"))
                .andExpect(jsonPath("$.quantity").value(2.5))
                .andExpect(jsonPath("$.unit").value("kg"))
                .andExpect(jsonPath("$.reason").value("Expired"))
                .andExpect(jsonPath("$.details").value("Expired in storage"));

        verify(wasteService, times(1)).registerWaste(any(WasteRequestDTO.class));
    }

    @Test
    void getAllWaste_ShouldReturnList() throws Exception {
        when(wasteService.getAllWaste()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/waste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].quantity").value(2.5))
                .andExpect(jsonPath("$[0].ingredientName").value("Harina"));

        verify(wasteService, times(1)).getAllWaste();
    }

    @Test
    void getWasteByIngredient_ShouldReturnList() throws Exception {
        Long ingredientId = 1L;
        when(wasteService.getWasteByIngredient(ingredientId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/waste/ingredient/{ingredientId}", ingredientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ingredientId").value(ingredientId))
                .andExpect(jsonPath("$[0].ingredientName").value("Harina"));

        verify(wasteService, times(1)).getWasteByIngredient(ingredientId);
    }

    @Test
    void deleteWaste_ShouldReturnOk() throws Exception {
        Long wasteId = 1L;
        doNothing().when(wasteService).deleteWaste(wasteId);

        mockMvc.perform(delete("/api/waste/{wasteId}", wasteId))
                .andExpect(status().isOk());

        verify(wasteService, times(1)).deleteWaste(wasteId);
    }


    @Test
    void registerWaste_WithMalformedJSON_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/waste")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        verify(wasteService, never()).registerWaste(any(WasteRequestDTO.class));
    }

}