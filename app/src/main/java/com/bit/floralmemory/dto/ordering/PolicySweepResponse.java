package com.bit.floralmemory.dto.ordering;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PolicySweepResponse {
    private Long sweepId;
    private BestPolicy best;

    @Data
    @Builder
    public static class BestPolicy {
        private Double coUnit;
        private Double cuUnit;
        private Double serviceLevel;
        private Double zValue;
        private Double sigmaInflation;
        private Double yhatShrink;
        private Double expectedTotalLoss;
    }
}
