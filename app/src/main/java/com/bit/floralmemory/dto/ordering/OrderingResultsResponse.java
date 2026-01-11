package com.bit.floralmemory.dto.ordering;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("unused")
public class OrderingResultsResponse {
    private Long orderingRunId;
    private List<Row> series;

    public OrderingResultsResponse() {
    }

    public OrderingResultsResponse(Long orderingRunId, List<Row> series) {
        this.orderingRunId = orderingRunId;
        this.series = series;
    }

    public static OrderingResultsResponseBuilder builder() {
        return new OrderingResultsResponseBuilder();
    }

    public Long getOrderingRunId() {
        return orderingRunId;
    }

    public void setOrderingRunId(Long orderingRunId) {
        this.orderingRunId = orderingRunId;
    }

    public List<Row> getSeries() {
        return series;
    }

    public void setSeries(List<Row> series) {
        this.series = series;
    }

    public static class OrderingResultsResponseBuilder {
        private Long orderingRunId;
        private List<Row> series;

        OrderingResultsResponseBuilder() {
        }

        public OrderingResultsResponseBuilder orderingRunId(Long orderingRunId) {
            this.orderingRunId = orderingRunId;
            return this;
        }

        public OrderingResultsResponseBuilder series(List<Row> series) {
            this.series = series;
            return this;
        }

        public OrderingResultsResponse build() {
            return new OrderingResultsResponse(orderingRunId, series);
        }
    }

    public static class Row {
        private LocalDate month;
        private Double yhat;
        private Double sigma;
        private Double serviceLevel;
        private Double zValue;
        private Double orderQty;
        private Double expectedWasteCost;
        private Double expectedStockoutLoss;
        private Double expectedTotalLoss;
        private String explanation;

        public Row() {
        }

        public Row(LocalDate month, Double yhat, Double sigma, Double serviceLevel, Double zValue, Double orderQty,
                Double expectedWasteCost, Double expectedStockoutLoss, Double expectedTotalLoss, String explanation) {
            this.month = month;
            this.yhat = yhat;
            this.sigma = sigma;
            this.serviceLevel = serviceLevel;
            this.zValue = zValue;
            this.orderQty = orderQty;
            this.expectedWasteCost = expectedWasteCost;
            this.expectedStockoutLoss = expectedStockoutLoss;
            this.expectedTotalLoss = expectedTotalLoss;
            this.explanation = explanation;
        }

        public static RowBuilder builder() {
            return new RowBuilder();
        }

        public LocalDate getMonth() {
            return month;
        }

        public void setMonth(LocalDate month) {
            this.month = month;
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

        public static class RowBuilder {
            private LocalDate month;
            private Double yhat;
            private Double sigma;
            private Double serviceLevel;
            private Double zValue;
            private Double orderQty;
            private Double expectedWasteCost;
            private Double expectedStockoutLoss;
            private Double expectedTotalLoss;
            private String explanation;

            RowBuilder() {
            }

            public RowBuilder month(LocalDate month) {
                this.month = month;
                return this;
            }

            public RowBuilder yhat(Double yhat) {
                this.yhat = yhat;
                return this;
            }

            public RowBuilder sigma(Double sigma) {
                this.sigma = sigma;
                return this;
            }

            public RowBuilder serviceLevel(Double serviceLevel) {
                this.serviceLevel = serviceLevel;
                return this;
            }

            public RowBuilder zValue(Double zValue) {
                this.zValue = zValue;
                return this;
            }

            public RowBuilder orderQty(Double orderQty) {
                this.orderQty = orderQty;
                return this;
            }

            public RowBuilder expectedWasteCost(Double expectedWasteCost) {
                this.expectedWasteCost = expectedWasteCost;
                return this;
            }

            public RowBuilder expectedStockoutLoss(Double expectedStockoutLoss) {
                this.expectedStockoutLoss = expectedStockoutLoss;
                return this;
            }

            public RowBuilder expectedTotalLoss(Double expectedTotalLoss) {
                this.expectedTotalLoss = expectedTotalLoss;
                return this;
            }

            public RowBuilder explanation(String explanation) {
                this.explanation = explanation;
                return this;
            }

            public Row build() {
                return new Row(month, yhat, sigma, serviceLevel, zValue, orderQty, expectedWasteCost,
                        expectedStockoutLoss, expectedTotalLoss, explanation);
            }
        }
    }
}
