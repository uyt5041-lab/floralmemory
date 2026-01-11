package com.bit.floralmemory.dto.ordering;

import lombok.Data;
import java.util.List;

@Data
public class PolicySweepRequest {
    private Long runId;
    private Grid grid;
    private Constraints constraints;

    @Data
    public static class Grid {
        private List<Double> coUnit;
        private List<Double> cuUnit;
        private List<Double> sigmaInflation;
        private List<Double> yhatShrink;
    }

    @Data
    public static class Constraints {
        private Double minServiceLevel;
        private Boolean maxOrderCapP90;
    }
}
