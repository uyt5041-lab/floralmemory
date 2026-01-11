package com.bit.floralmemory.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "forecast_ensemble", uniqueConstraints = @UniqueConstraint(columnNames = { "run_id", "month" }))
public class ForecastEnsemble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ensemble_id")
    private Long ensembleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private ModelRun run;

    @Column(name = "month", nullable = false)
    private LocalDate month;

    @Column(name = "yhat", nullable = false)
    private Double yhat;

    @Column(name = "sigma", nullable = false)
    private Double sigma;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "weights_json", nullable = false, columnDefinition = "jsonb")
    private String weightsJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public ForecastEnsemble() {
    }

    public ForecastEnsemble(Long ensembleId, ModelRun run, LocalDate month, Double yhat, Double sigma,
            String weightsJson, OffsetDateTime createdAt) {
        this.ensembleId = ensembleId;
        this.run = run;
        this.month = month;
        this.yhat = yhat;
        this.sigma = sigma;
        this.weightsJson = weightsJson;
        this.createdAt = createdAt;
    }

    public static ForecastEnsembleBuilder builder() {
        return new ForecastEnsembleBuilder();
    }

    public Long getEnsembleId() {
        return ensembleId;
    }

    public void setEnsembleId(Long ensembleId) {
        this.ensembleId = ensembleId;
    }

    public ModelRun getRun() {
        return run;
    }

    public void setRun(ModelRun run) {
        this.run = run;
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

    public String getWeightsJson() {
        return weightsJson;
    }

    public void setWeightsJson(String weightsJson) {
        this.weightsJson = weightsJson;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class ForecastEnsembleBuilder {
        private Long ensembleId;
        private ModelRun run;
        private LocalDate month;
        private Double yhat;
        private Double sigma;
        private String weightsJson;
        private OffsetDateTime createdAt;

        ForecastEnsembleBuilder() {
        }

        public ForecastEnsembleBuilder ensembleId(Long ensembleId) {
            this.ensembleId = ensembleId;
            return this;
        }

        public ForecastEnsembleBuilder run(ModelRun run) {
            this.run = run;
            return this;
        }

        public ForecastEnsembleBuilder month(LocalDate month) {
            this.month = month;
            return this;
        }

        public ForecastEnsembleBuilder yhat(Double yhat) {
            this.yhat = yhat;
            return this;
        }

        public ForecastEnsembleBuilder sigma(Double sigma) {
            this.sigma = sigma;
            return this;
        }

        public ForecastEnsembleBuilder weightsJson(String weightsJson) {
            this.weightsJson = weightsJson;
            return this;
        }

        public ForecastEnsembleBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ForecastEnsemble build() {
            return new ForecastEnsemble(ensembleId, run, month, yhat, sigma, weightsJson, createdAt);
        }
    }
}
