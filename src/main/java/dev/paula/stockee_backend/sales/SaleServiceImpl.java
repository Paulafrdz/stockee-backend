package dev.paula.stockee_backend.sales;

import org.springframework.stereotype.Service;

import dev.paula.stockee_backend.dish.DishEntity;
import dev.paula.stockee_backend.dish.DishRepository;
import dev.paula.stockee_backend.implementations.ISaleService;
import dev.paula.stockee_backend.stock.StockEntity;
import dev.paula.stockee_backend.stock.StockRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements ISaleService {

    private final SaleRepository saleRepository;
    private final DishRepository dishRepository;
    private final StockRepository stockRepository;

    @Override
    public SaleResponseDTO createSale(SaleRequestDTO request) {
        if (request.getLines() == null || request.getLines().isEmpty()) {
        throw new RuntimeException("La venta debe tener al menos un plato");
    }
        SaleEntity sale = new SaleEntity();
        sale.setDate(LocalDateTime.now());

        List<SaleLineEntity> lines = request.getLines().stream()
                .map(dto -> {
                    DishEntity dish = dishRepository.findById(dto.getDishId())
                            .orElseThrow(() -> new RuntimeException("Dish not found"));

                    // Discount each ingredient from stock
                    dish.getIngredients().forEach(ing -> {
                        StockEntity stock = ing.getIngredient();
                        double toDiscount = ing.getQuantity() * dto.getQuantity();
                        stock.setCurrentStock(stock.getCurrentStock() - toDiscount);
                        stockRepository.save(stock);
                    });

                    SaleLineEntity saleLine = new SaleLineEntity();
                    saleLine.setSale(sale);
                    saleLine.setDish(dish);
                    saleLine.setQuantity(dto.getQuantity());
                    return saleLine;
                })
                .collect(Collectors.toList());

        sale.setLines(lines);

        SaleEntity saved = saleRepository.save(sale);

        return convertToDTO(saved);
    }

    @Override
    public List<SaleResponseDTO> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSale(Long saleId) {
        SaleEntity sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new RuntimeException("Sale not found"));

        // Return stock for each line before deleting
        sale.getLines().forEach(line -> {
            line.getDish().getIngredients().forEach(ing -> {
                StockEntity stock = ing.getIngredient();
                double toReturn = ing.getQuantity() * line.getQuantity();
                stock.setCurrentStock(stock.getCurrentStock() + toReturn);
                stockRepository.save(stock);
            });
        });
    
        saleRepository.deleteById(saleId);
    }

    private SaleResponseDTO convertToDTO(SaleEntity sale) {

    List<SaleResponseDTO.SaleLineResponseDTO> lines = sale.getLines().stream()
            .map(line -> new SaleResponseDTO.SaleLineResponseDTO(
                    line.getDish().getId(),
                    line.getDish().getName(),
                    line.getDish().getIcon(),
                    line.getQuantity()
            ))
            .collect(Collectors.toList());

    return new SaleResponseDTO(
            sale.getId(),
            sale.getDate(),
            lines
    );
}
}
