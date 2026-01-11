package com.bit.floralmemory.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "forecast_result", uniqueConstraints = @UniqueConstraint(columnNames = { "run_id", "model_name",
        "month" }))
public class ForecastResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forecast_id")
    private Long forecastId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private ModelRun run;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "model_family", nullable = false)
    private String modelFamily;

    @Column(name = "month", nullable = false)
    private LocalDate month;

    @Column(name = "yhat", nullable = false)
    private Double yhat;

    @Column(name = "sigma", nullable = false)
    private Double sigma;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_json", nullable = false, columnDefinition = "jsonb")
    private String extraJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public ForecastResult() {
    }

    public ForecastResult(Long forecastId, ModelRun run, String modelName, String modelFamily, LocalDate month,
            Double yhat, Double sigma, String extraJson, OffsetDateTime createdAt) {
        this.forecastId = forecastId;
        this.run = run;
        this.modelName = modelName;
        this.modelFamily = modelFamily;
        this.month = month;
        this.yhat = yhat;
        this.sigma = sigma;
        this.extraJson = extraJson;
        this.createdAt = createdAt;
    }

    public static ForecastResultBuilder builder() {
        return new ForecastResultBuilder();
    }

    public Long getForecastId() {
        return forecastId;
    }

    public void setForecastId(Long forecastId) {
        this.forecastId = forecastId;
    }

    public ModelRun getRun() {
        return run;
    }

    public void setRun(ModelRun run) {
        this.run = run;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelFamily() {
        return modelFamily;
    }

    public void setModelFamily(String modelFamily) {
        this.modelFamily = modelFamily;
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

    public String getExtraJson() {
        return extraJson;
    }

    public void setExtraJson(String extraJson) {
        this.extraJson = extraJson;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class ForecastResultBuilder {
        private Long forecastId;
        private ModelRun run;
        private String modelName;
        private String modelFamily;
        private LocalDate month;
        private Double yhat;
        private Double sigma;
        private String extraJson;
        private OffsetDateTime createdAt;

        ForecastResultBuilder() {
        }

        public ForecastResultBuilder forecastId(Long forecastId) {
            this.forecastId = forecastId;
            return this;
        }

        public ForecastResultBuilder run(ModelRun run) {
            this.run = run;
            return this;
        }

        public ForecastResultBuilder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public ForecastResultBuilder modelFamily(String modelFamily) {
            this.modelFamily = modelFamily;
            return this;
        }

        public ForecastResultBuilder month(LocalDate month) {
            this.month = month;
            return this;
        }

        public ForecastResultBuilder yhat(Double yhat) {
            this.yhat = yhat;
            return this;
        }

        public ForecastResultBuilder sigma(Double sigma) {
            this.sigma = sigma;
            return this;
        }

        public ForecastResultBuilder extraJson(String extraJson) {
            this.extraJson = extraJson;
            return this;
        }

        public ForecastResultBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ForecastResult build() {
            return new ForecastResult(forecastId, run, modelName, modelFamily, month, yhat, sigma, extraJson,
                    createdAt);
        }
    }
}
