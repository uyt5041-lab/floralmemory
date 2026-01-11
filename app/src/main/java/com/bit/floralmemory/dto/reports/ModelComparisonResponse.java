package com.bit.floralmemory.dto.reports;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModelComparisonResponse {
    private Long runId;
    private List<Row> rows;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Row {
        private String modelName;
        private Double wape;
        private Double expectedTotalLoss;
    }
}
