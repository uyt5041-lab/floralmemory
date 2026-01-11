package com.bit.floralmemory.dto.ordering;

import java.util.List;

public class PolicySweepRequest {
    private String runId;
    private Grid grid;
    private Constraints constraints;

    public PolicySweepRequest() {
    }

    public PolicySweepRequest(String runId, Grid grid, Constraints constraints) {
        this.runId = runId;
        this.grid = grid;
        this.constraints = constraints;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public Constraints getConstraints() {
        return constraints;
    }

    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }

    public static class Grid {
        private java.util.List<Double> coUnit;
        private java.util.List<Double> cuUnit;
        private java.util.List<Double> sigmaInflation;
        private java.util.List<Double> yhatShrink;
        private Double defaultCo;
        private Double defaultCu;

        public Grid() {
        }

        public Grid(java.util.List<Double> coUnit, java.util.List<Double> cuUnit, java.util.List<Double> sigmaInflation,
                java.util.List<Double> yhatShrink, Double defaultCo, Double defaultCu) {
            this.coUnit = coUnit;
            this.cuUnit = cuUnit;
            this.sigmaInflation = sigmaInflation;
            this.yhatShrink = yhatShrink;
            this.defaultCo = defaultCo;
            this.defaultCu = defaultCu;
        }

        public java.util.List<Double> getCoUnit() {
            return coUnit;
        }

        public void setCoUnit(java.util.List<Double> coUnit) {
            this.coUnit = coUnit;
        }

        public java.util.List<Double> getCuUnit() {
            return cuUnit;
        }

        public void setCuUnit(java.util.List<Double> cuUnit) {
            this.cuUnit = cuUnit;
        }

        public java.util.List<Double> getSigmaInflation() {
            return sigmaInflation;
        }

        public void setSigmaInflation(java.util.List<Double> sigmaInflation) {
            this.sigmaInflation = sigmaInflation;
        }

        public java.util.List<Double> getYhatShrink() {
            return yhatShrink;
        }

        public void setYhatShrink(java.util.List<Double> yhatShrink) {
            this.yhatShrink = yhatShrink;
        }

        public Double getDefaultCo() {
            return defaultCo;
        }

        public void setDefaultCo(Double defaultCo) {
            this.defaultCo = defaultCo;
        }

        public Double getDefaultCu() {
            return defaultCu;
        }

        public void setDefaultCu(Double defaultCu) {
            this.defaultCu = defaultCu;
        }
    }

    public static class Constraints {
        private Double minServiceLevel;
        private Boolean maxOrderCapP90;

        public Constraints() {
        }

        public Constraints(Double minServiceLevel, Boolean maxOrderCapP90) {
            this.minServiceLevel = minServiceLevel;
            this.maxOrderCapP90 = maxOrderCapP90;
        }

        public Double getMinServiceLevel() {
            return minServiceLevel;
        }

        public void setMinServiceLevel(Double minServiceLevel) {
            this.minServiceLevel = minServiceLevel;
        }

        public Boolean getMaxOrderCapP90() {
            return maxOrderCapP90;
        }

        public void setMaxOrderCapP90(Boolean maxOrderCapP90) {
            this.maxOrderCapP90 = maxOrderCapP90;
        }
    }
}
