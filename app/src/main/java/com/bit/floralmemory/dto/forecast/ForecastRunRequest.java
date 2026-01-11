package com.bit.floralmemory.dto.forecast;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ForecastRunRequest {
    private Scope scope;
    private LocalDate trainStart;
    private LocalDate trainEnd;
    private LocalDate forecastStart;
    private LocalDate forecastEnd;
    private List<String> targets;
    private Models models;
    private Ensemble ensemble;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Scope {
        private String importer;
        private String exporter;
        private String productSlug;
        private List<String> hsCodes;
        private String granularity;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Models {
        private List<String> shortModels;
        private List<String> midModels;
        private List<String> longModels;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Ensemble {
        private boolean enabled;
        private Map<String, Map<String, Double>> weights;
    }
}
