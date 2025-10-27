package dev.paula.stockee_backend.analytics;

import lombok.Data;

@Data
public class AnalyticsStatsDTO {
    private Double totalWaste;
    private Double expiredWaste;
    private Double cookingErrorsWaste;
    private Double efficiency;
    private WasteTrendDTO trend;
}
