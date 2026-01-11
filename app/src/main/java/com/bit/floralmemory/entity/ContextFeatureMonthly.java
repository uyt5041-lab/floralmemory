package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "context_feature_monthly")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContextFeatureMonthly {

    @EmbeddedId
    private ContextFeatureId id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features_json", nullable = false, columnDefinition = "jsonb")
    private String featuresJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class ContextFeatureId implements java.io.Serializable {
        @Column(name = "scope_hash", nullable = false)
        private String scopeHash;

        @Column(name = "month", nullable = false)
        private LocalDate month;
    }
}
