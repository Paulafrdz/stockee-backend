package dev.paula.stockee_backend.analytics;

import lombok.Data;

@Data
public class WasteTrendDTO {
    private Boolean isPositive;
    private String text;
    private Double percentageChange;
}
