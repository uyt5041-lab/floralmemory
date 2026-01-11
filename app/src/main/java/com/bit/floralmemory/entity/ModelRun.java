package com.bit.floralmemory.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "model_run")
public class ModelRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "run_type", nullable = false)
    private String runType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scope_json", nullable = false, columnDefinition = "jsonb")
    private String scopeJson;

    @Column(name = "train_start", nullable = false)
    private LocalDate trainStart;

    @Column(name = "train_end", nullable = false)
    private LocalDate trainEnd;

    @Column(name = "forecast_start", nullable = false)
    private LocalDate forecastStart;

    @Column(name = "forecast_end", nullable = false)
    private LocalDate forecastEnd;

    @Column(name = "granularity", nullable = false)
    private String granularity;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public ModelRun() {
    }

    public ModelRun(Long runId, String runType, String scopeJson, LocalDate trainStart, LocalDate trainEnd,
            LocalDate forecastStart, LocalDate forecastEnd, String granularity, OffsetDateTime createdAt) {
        this.runId = runId;
        this.runType = runType;
        this.scopeJson = scopeJson;
        this.trainStart = trainStart;
        this.trainEnd = trainEnd;
        this.forecastStart = forecastStart;
        this.forecastEnd = forecastEnd;
        this.granularity = granularity;
        this.createdAt = createdAt;
    }

    public static ModelRunBuilder builder() {
        return new ModelRunBuilder();
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public String getRunType() {
        return runType;
    }

    public void setRunType(String runType) {
        this.runType = runType;
    }

    public String getScopeJson() {
        return scopeJson;
    }

    public void setScopeJson(String scopeJson) {
        this.scopeJson = scopeJson;
    }

    public LocalDate getTrainStart() {
        return trainStart;
    }

    public void setTrainStart(LocalDate trainStart) {
        this.trainStart = trainStart;
    }

    public LocalDate getTrainEnd() {
        return trainEnd;
    }

    public void setTrainEnd(LocalDate trainEnd) {
        this.trainEnd = trainEnd;
    }

    public LocalDate getForecastStart() {
        return forecastStart;
    }

    public void setForecastStart(LocalDate forecastStart) {
        this.forecastStart = forecastStart;
    }

    public LocalDate getForecastEnd() {
        return forecastEnd;
    }

    public void setForecastEnd(LocalDate forecastEnd) {
        this.forecastEnd = forecastEnd;
    }

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class ModelRunBuilder {
        private Long runId;
        private String runType;
        private String scopeJson;
        private LocalDate trainStart;
        private LocalDate trainEnd;
        private LocalDate forecastStart;
        private LocalDate forecastEnd;
        private String granularity;
        private OffsetDateTime createdAt;

        ModelRunBuilder() {
        }

        public ModelRunBuilder runId(Long runId) {
            this.runId = runId;
            return this;
        }

        public ModelRunBuilder runType(String runType) {
            this.runType = runType;
            return this;
        }

        public ModelRunBuilder scopeJson(String scopeJson) {
            this.scopeJson = scopeJson;
            return this;
        }

        public ModelRunBuilder trainStart(LocalDate trainStart) {
            this.trainStart = trainStart;
            return this;
        }

        public ModelRunBuilder trainEnd(LocalDate trainEnd) {
            this.trainEnd = trainEnd;
            return this;
        }

        public ModelRunBuilder forecastStart(LocalDate forecastStart) {
            this.forecastStart = forecastStart;
            return this;
        }

        public ModelRunBuilder forecastEnd(LocalDate forecastEnd) {
            this.forecastEnd = forecastEnd;
            return this;
        }

        public ModelRunBuilder granularity(String granularity) {
            this.granularity = granularity;
            return this;
        }

        public ModelRunBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ModelRun build() {
            return new ModelRun(runId, runType, scopeJson, trainStart, trainEnd, forecastStart, forecastEnd,
                    granularity, createdAt);
        }
    }
}
