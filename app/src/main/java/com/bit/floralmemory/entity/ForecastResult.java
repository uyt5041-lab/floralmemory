package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "forecast_result", uniqueConstraints = @UniqueConstraint(columnNames = { "run_id", "model_name",
        "month" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
