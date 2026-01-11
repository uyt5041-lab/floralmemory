package com.bit.floralmemory.dto.forecast;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ForecastResultsResponse {
    private Long runId;
    private String type;
    private List<Row> series;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Row {
        private LocalDate month;
        private Double yhat;
        private Double sigma;
        private Map<String, Double> weights;
    }
}
