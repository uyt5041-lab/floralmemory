package com.bit.floralmemory.dto.data;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImportSeriesResponse {
    private List<Row> series;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Row {
        private LocalDate month;
        private Double quantityKg;
        private Double valueUsd;
        private String hsCode;
    }
}
