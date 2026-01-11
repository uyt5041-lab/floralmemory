package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ordering_result")
@SuppressWarnings("unused")
public class OrderingResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_result_id")
    private Long orderResultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_run_id", nullable = false)
    private OrderingRun orderingRun;

    @Column(name = "sku_id", nullable = false)
    private String skuId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "yhat")
    private Double yhat;

    @Column(name = "sigma")
    private Double sigma;

    @Column(name = "service_level")
    private Double serviceLevel;

    @Column(name = "z_value")
    private Double zValue;

    @Column(name = "on_hand")
    private Double onHand;

    @Column(name = "order_qty")
    private Double orderQty;

    @Column(name = "expected_waste_cost")
    private Double expectedWasteCost;

    @Column(name = "expected_stockout_loss")
    private Double expectedStockoutLoss;

    @Column(name = "expected_total_loss")
    private Double expectedTotalLoss;

    @Column(name = "explanation", length = 1000)
    private String explanation;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public OrderingResult() {
    }

    public OrderingResult(Long orderResultId, OrderingRun orderingRun, String skuId, LocalDate date, Double yhat,
            Double sigma, Double serviceLevel, Double zValue, Double onHand, Double orderQty, Double expectedWasteCost,
            Double expectedStockoutLoss, Double expectedTotalLoss, String explanation, OffsetDateTime createdAt) {
        this.orderResultId = orderResultId;
        this.orderingRun = orderingRun;
        this.skuId = skuId;
        this.date = date;
        this.yhat = yhat;
        this.sigma = sigma;
        this.serviceLevel = serviceLevel;
        this.zValue = zValue;
        this.onHand = onHand;
        this.orderQty = orderQty;
        this.expectedWasteCost = expectedWasteCost;
        this.expectedStockoutLoss = expectedStockoutLoss;
        this.expectedTotalLoss = expectedTotalLoss;
        this.explanation = explanation;
        this.createdAt = createdAt;
    }

    public static OrderingResultBuilder builder() {
        return new OrderingResultBuilder();
    }

    public Long getOrderResultId() {
        return orderResultId;
    }

    public void setOrderResultId(Long orderResultId) {
        this.orderResultId = orderResultId;
    }

    public OrderingRun getOrderingRun() {
        return orderingRun;
    }

    public void setOrderingRun(OrderingRun orderingRun) {
        this.orderingRun = orderingRun;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getYhat() {
        return yhat;
    }

    public void setYhat(Double yhat) {
        this.yhat = yhat;
    }

    public Double getSigma() {
        return sigma;
    }

    public void setSigma(Double sigma) {
        this.sigma = sigma;
    }

    public Double getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(Double serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public Double getZValue() {
        return zValue;
    }

    public void setZValue(Double zValue) {
        this.zValue = zValue;
    }

    public Double getOnHand() {
        return onHand;
    }

    public void setOnHand(Double onHand) {
        this.onHand = onHand;
    }

    public Double getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Double orderQty) {
        this.orderQty = orderQty;
    }

    public Double getExpectedWasteCost() {
        return expectedWasteCost;
    }

    public void setExpectedWasteCost(Double expectedWasteCost) {
        this.expectedWasteCost = expectedWasteCost;
    }

    public Double getExpectedStockoutLoss() {
        return expectedStockoutLoss;
    }

    public void setExpectedStockoutLoss(Double expectedStockoutLoss) {
        this.expectedStockoutLoss = expectedStockoutLoss;
    }

    public Double getExpectedTotalLoss() {
        return expectedTotalLoss;
    }

    public void setExpectedTotalLoss(Double expectedTotalLoss) {
        this.expectedTotalLoss = expectedTotalLoss;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class OrderingResultBuilder {
        private Long orderResultId;
        private OrderingRun orderingRun;
        private String skuId;
        private LocalDate date;
        private Double yhat;
        private Double sigma;
        private Double serviceLevel;
        private Double zValue;
        private Double onHand;
        private Double orderQty;
        private Double expectedWasteCost;
        private Double expectedStockoutLoss;
        private Double expectedTotalLoss;
        private String explanation;
        private OffsetDateTime createdAt;

        OrderingResultBuilder() {
        }

        public OrderingResultBuilder orderResultId(Long id) {
            this.orderResultId = id;
            return this;
        }

        public OrderingResultBuilder orderingRun(OrderingRun run) {
            this.orderingRun = run;
            return this;
        }

        public OrderingResultBuilder skuId(String sku) {
            this.skuId = sku;
            return this;
        }

        public OrderingResultBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public OrderingResultBuilder yhat(Double y) {
            this.yhat = y;
            return this;
        }

        public OrderingResultBuilder sigma(Double s) {
            this.sigma = s;
            return this;
        }

        public OrderingResultBuilder serviceLevel(Double s) {
            this.serviceLevel = s;
            return this;
        }

        public OrderingResultBuilder zValue(Double z) {
            this.zValue = z;
            return this;
        }

        public OrderingResultBuilder onHand(Double o) {
            this.onHand = o;
            return this;
        }

        public OrderingResultBuilder orderQty(Double o) {
            this.orderQty = o;
            return this;
        }

        public OrderingResultBuilder expectedWasteCost(Double e) {
            this.expectedWasteCost = e;
            return this;
        }

        public OrderingResultBuilder expectedStockoutLoss(Double e) {
            this.expectedStockoutLoss = e;
            return this;
        }

        public OrderingResultBuilder expectedTotalLoss(Double e) {
            this.expectedTotalLoss = e;
            return this;
        }

        public OrderingResultBuilder explanation(String e) {
            this.explanation = e;
            return this;
        }

        public OrderingResultBuilder createdAt(OffsetDateTime c) {
            this.createdAt = c;
            return this;
        }

        public OrderingResult build() {
            return new OrderingResult(orderResultId, orderingRun, skuId, date, yhat, sigma, serviceLevel, zValue,
                    onHand, orderQty, expectedWasteCost, expectedStockoutLoss, expectedTotalLoss, explanation,
                    createdAt);
        }
    }
}
