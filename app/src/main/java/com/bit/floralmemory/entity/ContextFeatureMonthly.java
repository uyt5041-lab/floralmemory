package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "context_feature_monthly")
public class ContextFeatureMonthly {

    @EmbeddedId
    private ContextFeatureId id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features_json", nullable = false, columnDefinition = "jsonb")
    private String featuresJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public ContextFeatureMonthly() {
    }

    public ContextFeatureMonthly(ContextFeatureId id, String featuresJson, OffsetDateTime createdAt) {
        this.id = id;
        this.featuresJson = featuresJson;
        this.createdAt = createdAt;
    }

    public static ContextFeatureMonthlyBuilder builder() {
        return new ContextFeatureMonthlyBuilder();
    }

    public ContextFeatureId getId() {
        return id;
    }

    public String getFeaturesJson() {
        return featuresJson;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(ContextFeatureId id) {
        this.id = id;
    }

    public void setFeaturesJson(String featuresJson) {
        this.featuresJson = featuresJson;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Embeddable
    public static class ContextFeatureId implements java.io.Serializable {
        @Column(name = "scope_hash", nullable = false)
        private String scopeHash;

        @Column(name = "month", nullable = false)
        private LocalDate month;

        public ContextFeatureId() {
        }

        public ContextFeatureId(String scopeHash, LocalDate month) {
            this.scopeHash = scopeHash;
            this.month = month;
        }

        // Manual Equals/HashCode needed if using as ID?
        // Let's rely on standard impl or just generate simple ones.
        public String getScopeHash() {
            return scopeHash;
        }

        public LocalDate getMonth() {
            return month;
        }

        // Minimally valid equals/hashCode
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof ContextFeatureId))
                return false;
            ContextFeatureId that = (ContextFeatureId) o;
            return scopeHash.equals(that.scopeHash) && month.equals(that.month);
        }

        public int hashCode() {
            return java.util.Objects.hash(scopeHash, month);
        }
    }

    public static class ContextFeatureMonthlyBuilder {
        private ContextFeatureId id;
        private String featuresJson;
        private OffsetDateTime createdAt;

        ContextFeatureMonthlyBuilder() {
        }

        public ContextFeatureMonthlyBuilder id(ContextFeatureId id) {
            this.id = id;
            return this;
        }

        public ContextFeatureMonthlyBuilder featuresJson(String featuresJson) {
            this.featuresJson = featuresJson;
            return this;
        }

        public ContextFeatureMonthlyBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ContextFeatureMonthly build() {
            return new ContextFeatureMonthly(id, featuresJson, createdAt);
        }
    }
}
