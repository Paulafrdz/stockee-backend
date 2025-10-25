package dev.paula.stockee_backend.implementations;


import java.util.List;

import dev.paula.stockee_backend.waste.WasteRequestDTO;
import dev.paula.stockee_backend.waste.WasteResponseDTO;

public interface IWasteService {
    WasteResponseDTO registerWaste(WasteRequestDTO wasteRequest);
    List<WasteResponseDTO> getAllWaste();
    List<WasteResponseDTO> getWasteByIngredient(Long ingredientId);
    void deleteWaste(Long wasteId);
}