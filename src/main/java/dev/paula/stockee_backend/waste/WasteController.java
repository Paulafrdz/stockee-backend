package dev.paula.stockee_backend.waste;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/waste")
@RequiredArgsConstructor
public class WasteController {

    private final WasteService wasteService;

    @PostMapping
    public WasteResponseDTO registerWaste(@RequestBody WasteRequestDTO wasteRequest) {
        return wasteService.registerWaste(wasteRequest);
    }

    @GetMapping
    public List<WasteResponseDTO> getAllWaste() {
        return wasteService.getAllWaste();
    }

    @GetMapping("/ingredient/{ingredientId}")
    public List<WasteResponseDTO> getWasteByIngredient(@PathVariable Long ingredientId) {
        return wasteService.getWasteByIngredient(ingredientId);
    }

    @DeleteMapping("/{wasteId}")
    public void deleteWaste(@PathVariable Long wasteId) {
        wasteService.deleteWaste(wasteId);
    }
}