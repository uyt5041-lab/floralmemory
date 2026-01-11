package com.bit.floralmemory.dto.ordering;

import lombok.*;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderingRunRequest {
    private Long runId;
    private CostAssumption costAssumption;
    private Policy policy;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CostAssumption {
        private Double coUnit;
        private Double cuUnit;
        private String currency;
        private String rationale;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Policy {
        private Map<String, Object> serviceLevel;
        private Map<String, Object> safetyRules;
        private Map<String, Object> inventory;
    }
}
