package dev.paula.stockee_backend.lotes;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import dev.paula.stockee_backend.security.CurrentUserService;
import dev.paula.stockee_backend.user.UserEntity;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class LoteController {

    private final LoteRepository loteRepository;
    private final CurrentUserService currentUserService;

    @GetMapping("/{id}/lotes")
    public List<LoteResponseDTO> getLotesByStock(@PathVariable Long id) {
        UserEntity user = currentUserService.get();

        return loteRepository.findByStockIdAndUserOrderByExpiryDateAsc(id, user)
            .stream()
            .map(l -> LoteResponseDTO.builder()
                .id(l.getId())
                .quantity(l.getQuantity())
                .unit(l.getUnit())
                .orderDate(l.getOrderDate())
                .expiryDate(l.getExpiryDate())
                .build())
            .collect(Collectors.toList());
    }
}