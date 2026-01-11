package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "policy_sweep_result")
public class PolicySweepResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sweep_result_id")
    private Long sweepResultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sweep_id", nullable = false)
    private PolicySweepRun sweepRun;

    @Column(name = "co_unit", nullable = false)
    private Double coUnit;

    @Column(name = "cu_unit", nullable = false)
    private Double cuUnit;

    @Column(name = "service_level", nullable = false)
    private Double serviceLevel;

    @Column(name = "z_value", nullable = false)
    private Double zValue;

    @Column(name = "sigma_inflation", nullable = false)
    private Double sigmaInflation;

    @Column(name = "yhat_shrink", nullable = false)
    private Double yhatShrink;

    @Column(name = "expected_total_loss")
    private Double expectedTotalLoss;

    @Column(name = "expected_waste_cost")
    private Double expectedWasteCost;

    @Column(name = "expected_stockout_loss")
    private Double expectedStockoutLoss;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "constraints_json", nullable = false, columnDefinition = "jsonb")
    private String constraintsJson;

    @Column(name = "is_best", nullable = false)
    private Boolean isBest = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public PolicySweepResult() {
    }

    public PolicySweepResult(Long sweepResultId, PolicySweepRun sweepRun, Double coUnit, Double cuUnit,
            Double serviceLevel, Double zValue, Double sigmaInflation, Double yhatShrink, Double expectedTotalLoss,
            Double expectedWasteCost, Double expectedStockoutLoss, String constraintsJson, Boolean isBest,
            OffsetDateTime createdAt) {
        this.sweepResultId = sweepResultId;
        this.sweepRun = sweepRun;
        this.coUnit = coUnit;
        this.cuUnit = cuUnit;
        this.serviceLevel = serviceLevel;
        this.zValue = zValue;
        this.sigmaInflation = sigmaInflation;
        this.yhatShrink = yhatShrink;
        this.expectedTotalLoss = expectedTotalLoss;
        this.expectedWasteCost = expectedWasteCost;
        this.expectedStockoutLoss = expectedStockoutLoss;
        this.constraintsJson = constraintsJson;
        this.isBest = isBest != null ? isBest : false;
        this.createdAt = createdAt;
    }

    public static PolicySweepResultBuilder builder() {
        return new PolicySweepResultBuilder();
    }

    // Getters
    public Long getSweepResultId() {
        return sweepResultId;
    }

    public PolicySweepRun getSweepRun() {
        return sweepRun;
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

    public Double getExpectedWasteCost() {
        return expectedWasteCost;
    }

    public Double getExpectedStockoutLoss() {
        return expectedStockoutLoss;
    }

    public String getConstraintsJson() {
        return constraintsJson;
    }

    public Boolean getIsBest() {
        return isBest;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setSweepResultId(Long sweepResultId) {
        this.sweepResultId = sweepResultId;
    }

    public void setSweepRun(PolicySweepRun sweepRun) {
        this.sweepRun = sweepRun;
    }

    public void setCoUnit(Double coUnit) {
        this.coUnit = coUnit;
    }

    public void setCuUnit(Double cuUnit) {
        this.cuUnit = cuUnit;
    }

    public void setServiceLevel(Double serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public void setZValue(Double zValue) {
        this.zValue = zValue;
    }

    public void setSigmaInflation(Double sigmaInflation) {
        this.sigmaInflation = sigmaInflation;
    }

    public void setYhatShrink(Double yhatShrink) {
        this.yhatShrink = yhatShrink;
    }

    public void setExpectedTotalLoss(Double expectedTotalLoss) {
        this.expectedTotalLoss = expectedTotalLoss;
    }

    public void setExpectedWasteCost(Double expectedWasteCost) {
        this.expectedWasteCost = expectedWasteCost;
    }

    public void setExpectedStockoutLoss(Double expectedStockoutLoss) {
        this.expectedStockoutLoss = expectedStockoutLoss;
    }

    public void setConstraintsJson(String constraintsJson) {
        this.constraintsJson = constraintsJson;
    }

    public void setIsBest(Boolean isBest) {
        this.isBest = isBest;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class PolicySweepResultBuilder {
        private Long sweepResultId;
        private PolicySweepRun sweepRun;
        private Double coUnit;
        private Double cuUnit;
        private Double serviceLevel;
        private Double zValue;
        private Double sigmaInflation;
        private Double yhatShrink;
        private Double expectedTotalLoss;
        private Double expectedWasteCost;
        private Double expectedStockoutLoss;
        private String constraintsJson;
        private Boolean isBest;
        private OffsetDateTime createdAt;

        PolicySweepResultBuilder() {
        }

        public PolicySweepResultBuilder sweepResultId(Long sweepResultId) {
            this.sweepResultId = sweepResultId;
            return this;
        }

        public PolicySweepResultBuilder sweepRun(PolicySweepRun sweepRun) {
            this.sweepRun = sweepRun;
            return this;
        }

        public PolicySweepResultBuilder coUnit(Double coUnit) {
            this.coUnit = coUnit;
            return this;
        }

        public PolicySweepResultBuilder cuUnit(Double cuUnit) {
            this.cuUnit = cuUnit;
            return this;
        }

        public PolicySweepResultBuilder serviceLevel(Double serviceLevel) {
            this.serviceLevel = serviceLevel;
            return this;
        }

        public PolicySweepResultBuilder zValue(Double zValue) {
            this.zValue = zValue;
            return this;
        }

        public PolicySweepResultBuilder sigmaInflation(Double sigmaInflation) {
            this.sigmaInflation = sigmaInflation;
            return this;
        }

        public PolicySweepResultBuilder yhatShrink(Double yhatShrink) {
            this.yhatShrink = yhatShrink;
            return this;
        }

        public PolicySweepResultBuilder expectedTotalLoss(Double expectedTotalLoss) {
            this.expectedTotalLoss = expectedTotalLoss;
            return this;
        }

        public PolicySweepResultBuilder expectedWasteCost(Double expectedWasteCost) {
            this.expectedWasteCost = expectedWasteCost;
            return this;
        }

        public PolicySweepResultBuilder expectedStockoutLoss(Double expectedStockoutLoss) {
            this.expectedStockoutLoss = expectedStockoutLoss;
            return this;
        }

        public PolicySweepResultBuilder constraintsJson(String constraintsJson) {
            this.constraintsJson = constraintsJson;
            return this;
        }

        public PolicySweepResultBuilder isBest(Boolean isBest) {
            this.isBest = isBest;
            return this;
        }

        public PolicySweepResultBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PolicySweepResult build() {
            return new PolicySweepResult(sweepResultId, sweepRun, coUnit, cuUnit, serviceLevel, zValue, sigmaInflation,
                    yhatShrink, expectedTotalLoss, expectedWasteCost, expectedStockoutLoss, constraintsJson, isBest,
                    createdAt);
        }
    }
}
