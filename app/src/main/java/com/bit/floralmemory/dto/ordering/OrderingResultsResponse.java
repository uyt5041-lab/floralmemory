package com.bit.floralmemory.dto.ordering;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderingResultsResponse {
    private Long orderingRunId;
    private List<Row> series;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Row {
        private LocalDate month;
        private Double yhat;
        private Double sigma;
        private Double serviceLevel;
        private Double zValue;
        private Double orderQty;
        private Double expectedWasteCost;
        private Double expectedStockoutLoss;
        private Double expectedTotalLoss;
        private String explanation;
    }
}
