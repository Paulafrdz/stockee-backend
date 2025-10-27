package dev.paula.stockee_backend.analytics;

import dev.paula.stockee_backend.waste.WasteService;
import dev.paula.stockee_backend.waste.WasteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final WasteService wasteService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAnalyticsStats() {
        try {
            List<WasteResponseDTO> allWaste = wasteService.getAllWaste();
            
            LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay();
            
            List<WasteResponseDTO> currentMonthWaste = allWaste.stream()
                .filter(waste -> {
                    LocalDateTime wasteTime = waste.getTimestamp();
                    return wasteTime != null && 
                           wasteTime.isAfter(startOfMonth) && 
                           wasteTime.isBefore(endOfMonth);
                })
                .collect(Collectors.toList());

            double totalWaste = currentMonthWaste.stream()
                .mapToDouble(WasteResponseDTO::getQuantity)
                .sum();

            // UPDATED: English reason values
            double expiredWaste = currentMonthWaste.stream()
                .filter(w -> "expired".equals(w.getReason()))  
                .mapToDouble(WasteResponseDTO::getQuantity)
                .sum();

            double cookingErrorsWaste = currentMonthWaste.stream()
                .filter(w -> "burned".equals(w.getReason()) ||           
                            "wrong-ingredient".equals(w.getReason()) ||  
                            "over-preparation".equals(w.getReason()))    
                .mapToDouble(WasteResponseDTO::getQuantity)
                .sum();

            long expiredCount = currentMonthWaste.stream()
                .filter(w -> "expired".equals(w.getReason()))  
                .count();

            long cookingErrorsCount = currentMonthWaste.stream()
                .filter(w -> "burned".equals(w.getReason()) ||           
                            "wrong-ingredient".equals(w.getReason()) ||  
                            "over-preparation".equals(w.getReason()))    
                .count();

            double efficiency = totalWaste > 0 ? Math.max(0, 100 - (totalWaste / 10 * 100)) : 100;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalWaste", Math.round(totalWaste * 100.0) / 100.0);
            stats.put("expiredWaste", Math.round(expiredWaste * 100.0) / 100.0);
            stats.put("cookingErrorsWaste", Math.round(cookingErrorsWaste * 100.0) / 100.0);
            stats.put("efficiency", Math.round(efficiency));
            stats.put("expiredCount", expiredCount);
            stats.put("cookingErrorsCount", cookingErrorsCount);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/waste-types")
    public ResponseEntity<List<Map<String, Object>>> getWasteTypes() {
        try {
            List<WasteResponseDTO> allWaste = wasteService.getAllWaste();
            
            Map<String, Double> wasteByType = allWaste.stream()
                .collect(Collectors.groupingBy(
                    waste -> {
                        String reason = waste.getReason();

                        if ("expired".equals(reason)) return "Expired";
                        if ("burned".equals(reason) || 
                            "wrong-ingredient".equals(reason) || 
                            "over-preparation".equals(reason)) return "Cooking Errors";
                        if ("breakage".equals(reason)) return "Breakage";
                        if ("natural-waste".equals(reason)) return "Natural Waste";
                        return "Other";
                    },
                    Collectors.summingDouble(WasteResponseDTO::getQuantity)
                ));

            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Map.Entry<String, Double> entry : wasteByType.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("type", entry.getKey());
                item.put("amount", Math.round(entry.getValue() * 100.0) / 100.0);
                result.add(item);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/waste-trend")
    public ResponseEntity<List<Map<String, Object>>> getWasteTrend() {
        try {
            List<WasteResponseDTO> allWaste = wasteService.getAllWaste();
            
            List<Map<String, Object>> trend = new ArrayList<>();
            
            Map<String, Double> monthlyWaste = allWaste.stream()
                .collect(Collectors.groupingBy(
                    waste -> {
                        LocalDateTime timestamp = waste.getTimestamp();
                        if (timestamp == null) return "Unknown";
                        return timestamp.getMonth().toString().substring(0, 3); 
                    },
                    Collectors.summingDouble(WasteResponseDTO::getQuantity)
                ));
            
            // If no data, return sample data
            if (monthlyWaste.isEmpty()) {
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"}; 
                Random random = new Random();
                
                for (String month : months) {
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("month", month);
                    monthData.put("value", Math.round((30 + random.nextDouble() * 20) * 10.0) / 10.0);
                    trend.add(monthData);
                }
            } else {
                // Use actual grouped data
                for (Map.Entry<String, Double> entry : monthlyWaste.entrySet()) {
                    Map<String, Object> monthData = new HashMap<>();
                    monthData.put("month", entry.getKey());
                    monthData.put("value", Math.round(entry.getValue() * 10.0) / 10.0);
                    trend.add(monthData);
                }
            }
            
            trend.sort((a, b) -> {
                String[] monthOrder = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                String monthA = (String) a.get("month");
                String monthB = (String) b.get("month");
                return Integer.compare(
                    Arrays.asList(monthOrder).indexOf(monthA),
                    Arrays.asList(monthOrder).indexOf(monthB)
                );
            });

            return ResponseEntity.ok(trend);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}