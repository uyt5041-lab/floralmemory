package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "forecast_ensemble",
       uniqueConstraints = @UniqueConstraint(columnNames = {"run_id","month"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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
}
