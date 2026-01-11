package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "policy_sweep_run")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicySweepRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sweep_id")
    private Long sweepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private ModelRun run;

    @Column(name = "objective", nullable = false)
    @Builder.Default
    private String objective = "MIN_EXPECTED_TOTAL_LOSS";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "grid_json", nullable = false, columnDefinition = "jsonb")
    private String gridJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
