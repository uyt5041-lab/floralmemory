package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ordering_run")
public class OrderingRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_run_id")
    private Long orderRunId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_run_id", nullable = false)
    private ModelRun modelRun;

    @Column(name = "sl_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String slJson;

    @Column(name = "safety_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String safetyJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public OrderingRun() {
    }

    public OrderingRun(Long orderRunId, ModelRun modelRun, String slJson, String safetyJson, OffsetDateTime createdAt) {
        this.orderRunId = orderRunId;
        this.modelRun = modelRun;
        this.slJson = slJson;
        this.safetyJson = safetyJson;
        this.createdAt = createdAt;
    }

    public static OrderingRunBuilder builder() {
        return new OrderingRunBuilder();
    }

    public Long getOrderRunId() {
        return orderRunId;
    }

    public void setOrderRunId(Long orderRunId) {
        this.orderRunId = orderRunId;
    }

    public ModelRun getModelRun() {
        return modelRun;
    }

    public void setModelRun(ModelRun modelRun) {
        this.modelRun = modelRun;
    }

    public String getSlJson() {
        return slJson;
    }

    public void setSlJson(String slJson) {
        this.slJson = slJson;
    }

    public String getSafetyJson() {
        return safetyJson;
    }

    public void setSafetyJson(String safetyJson) {
        this.safetyJson = safetyJson;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class OrderingRunBuilder {
        private Long orderRunId;
        private ModelRun modelRun;
        private String slJson;
        private String safetyJson;
        private OffsetDateTime createdAt;

        OrderingRunBuilder() {
        }

        public OrderingRunBuilder orderRunId(Long orderRunId) {
            this.orderRunId = orderRunId;
            return this;
        }

        public OrderingRunBuilder modelRun(ModelRun modelRun) {
            this.modelRun = modelRun;
            return this;
        }

        public OrderingRunBuilder slJson(String slJson) {
            this.slJson = slJson;
            return this;
        }

        public OrderingRunBuilder safetyJson(String safetyJson) {
            this.safetyJson = safetyJson;
            return this;
        }

        public OrderingRunBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public OrderingRun build() {
            return new OrderingRun(orderRunId, modelRun, slJson, safetyJson, createdAt);
        }
    }
}
