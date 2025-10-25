package dev.paula.stockee_backend.waste;

import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WasteServiceImpl implements WasteService {

    private final WasteRepository wasteRepository;
    private final StockRepository stockRepository;

    @Override
    @Transactional
    public WasteResponseDTO registerWaste(WasteRequestDTO wasteRequest) {
        StockEntity ingredient = stockRepository.findById(wasteRequest.getIngredientId())
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado"));

        if (ingredient.getCurrentStock() < wasteRequest.getQuantity()) {
            throw new RuntimeException("Stock insuficiente para registrar el desperdicio");
        }

        // Actualizar stock
        double newStock = ingredient.getCurrentStock() - wasteRequest.getQuantity();
        ingredient.setCurrentStock(newStock);
        stockRepository.save(ingredient);

        // Crear waste
        WasteEntity waste = new WasteEntity(
            ingredient,
            wasteRequest.getQuantity(),
            wasteRequest.getUnit(),
            wasteRequest.getReason(),
            wasteRequest.getDetails()
        );

        WasteEntity savedWaste = wasteRepository.save(waste);
        return convertToDTO(savedWaste);
    }

    @Override
    public List<WasteResponseDTO> getAllWaste() {
        return wasteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<WasteResponseDTO> getWasteByIngredient(Long ingredientId) {
        return wasteRepository.findByIngredientId(ingredientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteWaste(Long wasteId) {
        WasteEntity waste = wasteRepository.findById(wasteId)
                .orElseThrow(() -> new RuntimeException("Registro de desperdicio no encontrado"));

        // Revertir stock
        StockEntity ingredient = waste.getIngredient();
        ingredient.setCurrentStock(ingredient.getCurrentStock() + waste.getQuantity());
        stockRepository.save(ingredient);

        wasteRepository.delete(waste);
    }

    private WasteResponseDTO convertToDTO(WasteEntity waste) {
        return new WasteResponseDTO(
            waste.getId(),
            waste.getIngredient().getId(),
            waste.getIngredient().getName(),
            waste.getQuantity(),
            waste.getUnit(),
            waste.getReason(),
            waste.getDetails(),
            waste.getTimestamp()
        );
    }
}