package com.bit.floralmemory.dto.ordering;

import lombok.Builder;
import lombok.Data;

public class PolicySweepResponse {
    private Long sweepId;
    private BestPolicy best;

    public PolicySweepResponse() {
    }

    public PolicySweepResponse(Long sweepId, BestPolicy best) {
        this.sweepId = sweepId;
        this.best = best;
    }

    public static PolicySweepResponseBuilder builder() {
        return new PolicySweepResponseBuilder();
    }

    public Long getSweepId() {
        return sweepId;
    }

    public BestPolicy getBest() {
        return best;
    }

    public void setSweepId(Long sweepId) {
        this.sweepId = sweepId;
    }

    public void setBest(BestPolicy best) {
        this.best = best;
    }

    public static class BestPolicy {
        private Double coUnit;
        private Double cuUnit;
        private Double serviceLevel;
        private Double zValue;
        private Double sigmaInflation;
        private Double yhatShrink;
        private Double expectedTotalLoss;

        public BestPolicy() {
        }

        public BestPolicy(Double coUnit, Double cuUnit, Double serviceLevel, Double zValue, Double sigmaInflation,
                Double yhatShrink, Double expectedTotalLoss) {
            this.coUnit = coUnit;
            this.cuUnit = cuUnit;
            this.serviceLevel = serviceLevel;
            this.zValue = zValue;
            this.sigmaInflation = sigmaInflation;
            this.yhatShrink = yhatShrink;
            this.expectedTotalLoss = expectedTotalLoss;
        }

        public static BestPolicyBuilder builder() {
            return new BestPolicyBuilder();
        }

        public Double getCoUnit() {
            return coUnit;
        }

        public Double getCuUnit() {
            return cuUnit;
        }

        public Double getServiceLevel() {
            return serviceLevel;
        }

        public Double getZValue() {
            return zValue;
        }

        public Double getSigmaInflation() {
            return sigmaInflation;
        }

        public Double getYhatShrink() {
            return yhatShrink;
        }

        public Double getExpectedTotalLoss() {
            return expectedTotalLoss;
        }

        public static class BestPolicyBuilder {
            private Double coUnit;
            private Double cuUnit;
            private Double serviceLevel;
            private Double zValue;
            private Double sigmaInflation;
            private Double yhatShrink;
            private Double expectedTotalLoss;

            BestPolicyBuilder() {
            }

            public BestPolicyBuilder coUnit(Double v) {
                this.coUnit = v;
                return this;
            }

            public BestPolicyBuilder cuUnit(Double v) {
                this.cuUnit = v;
                return this;
            }

            public BestPolicyBuilder serviceLevel(Double v) {
                this.serviceLevel = v;
                return this;
            }

            public BestPolicyBuilder zValue(Double v) {
                this.zValue = v;
                return this;
            }

            public BestPolicyBuilder sigmaInflation(Double v) {
                this.sigmaInflation = v;
                return this;
            }

            public BestPolicyBuilder yhatShrink(Double v) {
                this.yhatShrink = v;
                return this;
            }

            public BestPolicyBuilder expectedTotalLoss(Double v) {
                this.expectedTotalLoss = v;
                return this;
            }

            public BestPolicy build() {
                return new BestPolicy(coUnit, cuUnit, serviceLevel, zValue, sigmaInflation, yhatShrink,
                        expectedTotalLoss);
            }
        }
    }

    public static class PolicySweepResponseBuilder {
        private Long sweepId;
        private BestPolicy best;

        PolicySweepResponseBuilder() {
        }

        public PolicySweepResponseBuilder sweepId(Long sweepId) {
            this.sweepId = sweepId;
            return this;
        }

        public PolicySweepResponseBuilder best(BestPolicy best) {
            this.best = best;
            return this;
        }

        public PolicySweepResponse build() {
            return new PolicySweepResponse(sweepId, best);
        }
    }
}
