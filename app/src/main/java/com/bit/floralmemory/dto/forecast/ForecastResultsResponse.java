package com.bit.floralmemory.dto.forecast;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ForecastResultsResponse {
    private Long runId;
    private String type;
    private List<Row> series;

    public ForecastResultsResponse() {
    }

    public ForecastResultsResponse(Long runId, String type, List<Row> series) {
        this.runId = runId;
        this.type = type;
        this.series = series;
    }

    public static ForecastResultsResponseBuilder builder() {
        return new ForecastResultsResponseBuilder();
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Row> getSeries() {
        return series;
    }

    public void setSeries(List<Row> series) {
        this.series = series;
    }

    public static class ForecastResultsResponseBuilder {
        private Long runId;
        private String type;
        private List<Row> series;

        ForecastResultsResponseBuilder() {
        }

        public ForecastResultsResponseBuilder runId(Long runId) {
            this.runId = runId;
            return this;
        }

        public ForecastResultsResponseBuilder type(String type) {
            this.type = type;
            return this;
        }

        public ForecastResultsResponseBuilder series(List<Row> series) {
            this.series = series;
            return this;
        }

        public ForecastResultsResponse build() {
            return new ForecastResultsResponse(runId, type, series);
        }
    }

    public static class Row {
        private LocalDate month;
        private Double yhat;
        private Double sigma;
        private Map<String, Double> weights;

        public Row() {
        }

        public Row(LocalDate month, Double yhat, Double sigma, Map<String, Double> weights) {
            this.month = month;
            this.yhat = yhat;
            this.sigma = sigma;
            this.weights = weights;
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

        public Map<String, Double> getWeights() {
            return weights;
        }

        public void setWeights(Map<String, Double> weights) {
            this.weights = weights;
        }

        public static class RowBuilder {
            private LocalDate month;
            private Double yhat;
            private Double sigma;
            private Map<String, Double> weights;

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

            public RowBuilder weights(Map<String, Double> weights) {
                this.weights = weights;
                return this;
            }

            public Row build() {
                return new Row(month, yhat, sigma, weights);
            }
        }
    }
}
