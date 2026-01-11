package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "policy_sweep_run")
public class PolicySweepRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sweep_id")
    private Long sweepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private ModelRun run;

    @Column(name = "objective", nullable = false)
    private String objective = "MIN_EXPECTED_TOTAL_LOSS";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "grid_json", nullable = false, columnDefinition = "jsonb")
    private String gridJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public PolicySweepRun() {
    }

    public PolicySweepRun(Long sweepId, ModelRun run, String objective, String gridJson, OffsetDateTime createdAt) {
        this.sweepId = sweepId;
        this.run = run;
        this.objective = objective != null ? objective : "MIN_EXPECTED_TOTAL_LOSS";
        this.gridJson = gridJson;
        this.createdAt = createdAt;
    }

    public static PolicySweepRunBuilder builder() {
        return new PolicySweepRunBuilder();
    }

    // Getters
    public Long getSweepId() {
        return sweepId;
    }

    public ModelRun getRun() {
        return run;
    }

    public String getObjective() {
        return objective;
    }

    public String getGridJson() {
        return gridJson;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setSweepId(Long sweepId) {
        this.sweepId = sweepId;
    }

    public void setRun(ModelRun run) {
        this.run = run;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setGridJson(String gridJson) {
        this.gridJson = gridJson;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class PolicySweepRunBuilder {
        private Long sweepId;
        private ModelRun run;
        private String objective;
        private String gridJson;
        private OffsetDateTime createdAt;

        PolicySweepRunBuilder() {
        }

        public PolicySweepRunBuilder sweepId(Long sweepId) {
            this.sweepId = sweepId;
            return this;
        }

        public PolicySweepRunBuilder run(ModelRun run) {
            this.run = run;
            return this;
        }

        public PolicySweepRunBuilder objective(String objective) {
            this.objective = objective;
            return this;
        }

        public PolicySweepRunBuilder gridJson(String gridJson) {
            this.gridJson = gridJson;
            return this;
        }

        public PolicySweepRunBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PolicySweepRun build() {
            return new PolicySweepRun(sweepId, run, objective, gridJson, createdAt);
        }
    }
}
