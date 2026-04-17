package dev.paula.stockee_backend.lotes;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class LoteController {

    private final LoteRepository loteRepository;

    @GetMapping("/{id}/lotes")
    public List<LoteResponseDTO> getLotesByStock(@PathVariable Long id) {
        return loteRepository.findByStockIdOrderByExpiryDateAsc(id)
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