package dev.paula.stockee_backend.lotes;

import dev.paula.stockee_backend.security.CurrentUserService;
import dev.paula.stockee_backend.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteControllerTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private LoteController loteController;

    @Test
    void getLotesByStock_shouldReturnMappedDTOs() {

        // ===== USER =====
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("test");

        when(currentUserService.get()).thenReturn(user);

        // ===== LOTES =====
        LoteEntity lote = new LoteEntity();
        lote.setId(10L);
        lote.setQuantity(5.0);
        lote.setUnit("kg");
        lote.setOrderDate(LocalDate.now());
        lote.setExpiryDate(LocalDate.now().plusDays(10));

        when(loteRepository.findByStockIdAndUserOrderByExpiryDateAsc(1L, user))
                .thenReturn(List.of(lote));

        // ===== EXECUTE =====
        List<LoteResponseDTO> result = loteController.getLotesByStock(1L);

        // ===== VERIFY =====
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(5.0, result.get(0).getQuantity());

        verify(loteRepository).findByStockIdAndUserOrderByExpiryDateAsc(1L, user);
        verify(currentUserService).get();
    }
}