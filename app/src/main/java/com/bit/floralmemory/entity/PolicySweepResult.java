package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "policy_sweep_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public Double getSigmaInflation() {
        return this.sigmaInflation;
    }

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
    @Builder.Default
    private Boolean isBest = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
