package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "model_run")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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
}
