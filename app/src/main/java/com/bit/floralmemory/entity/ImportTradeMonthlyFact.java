package com.bit.floralmemory.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "import_trade_monthly_fact", uniqueConstraints = @UniqueConstraint(columnNames = { "month",
        "importer_code", "exporter_code", "hs_code", "source_name" }))
public class ImportTradeMonthlyFact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fact_id")
    private Long factId;

    @Column(name = "month", nullable = false)
    private LocalDate month;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "importer_code", nullable = false)
    private DimCountry importer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exporter_code", nullable = false)
    private DimCountry exporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hs_code", nullable = false)
    private DimHsCode hsCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private DimProduct product;

    @Column(name = "quantity_kg", nullable = false)
    private Double quantityKg;

    @Column(name = "value_usd", nullable = false)
    private Double valueUsd;

    @Column(name = "source_name", nullable = false)
    private String sourceName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "quality_flags", nullable = false, columnDefinition = "jsonb")
    private String qualityFlags;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public ImportTradeMonthlyFact() {
    }

    public ImportTradeMonthlyFact(Long factId, LocalDate month, DimCountry importer, DimCountry exporter,
            DimHsCode hsCode, DimProduct product, Double quantityKg, Double valueUsd, String sourceName,
            String qualityFlags, OffsetDateTime createdAt) {
        this.factId = factId;
        this.month = month;
        this.importer = importer;
        this.exporter = exporter;
        this.hsCode = hsCode;
        this.product = product;
        this.quantityKg = quantityKg;
        this.valueUsd = valueUsd;
        this.sourceName = sourceName;
        this.qualityFlags = qualityFlags;
        this.createdAt = createdAt;
    }

    public static ImportTradeMonthlyFactBuilder builder() {
        return new ImportTradeMonthlyFactBuilder();
    }

    public Long getFactId() {
        return factId;
    }

    public void setFactId(Long factId) {
        this.factId = factId;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public DimCountry getImporter() {
        return importer;
    }

    public void setImporter(DimCountry importer) {
        this.importer = importer;
    }

    public DimCountry getExporter() {
        return exporter;
    }

    public void setExporter(DimCountry exporter) {
        this.exporter = exporter;
    }

    public DimHsCode getHsCode() {
        return hsCode;
    }

    public void setHsCode(DimHsCode hsCode) {
        this.hsCode = hsCode;
    }

    public DimProduct getProduct() {
        return product;
    }

    public void setProduct(DimProduct product) {
        this.product = product;
    }

    public Double getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(Double quantityKg) {
        this.quantityKg = quantityKg;
    }

    public Double getValueUsd() {
        return valueUsd;
    }

    public void setValueUsd(Double valueUsd) {
        this.valueUsd = valueUsd;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getQualityFlags() {
        return qualityFlags;
    }

    public void setQualityFlags(String qualityFlags) {
        this.qualityFlags = qualityFlags;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class ImportTradeMonthlyFactBuilder {
        private Long factId;
        private LocalDate month;
        private DimCountry importer;
        private DimCountry exporter;
        private DimHsCode hsCode;
        private DimProduct product;
        private Double quantityKg;
        private Double valueUsd;
        private String sourceName;
        private String qualityFlags;
        private OffsetDateTime createdAt;

        ImportTradeMonthlyFactBuilder() {
        }

        public ImportTradeMonthlyFactBuilder factId(Long factId) {
            this.factId = factId;
            return this;
        }

        public ImportTradeMonthlyFactBuilder month(LocalDate month) {
            this.month = month;
            return this;
        }

        public ImportTradeMonthlyFactBuilder importer(DimCountry importer) {
            this.importer = importer;
            return this;
        }

        public ImportTradeMonthlyFactBuilder exporter(DimCountry exporter) {
            this.exporter = exporter;
            return this;
        }

        public ImportTradeMonthlyFactBuilder hsCode(DimHsCode hsCode) {
            this.hsCode = hsCode;
            return this;
        }

        public ImportTradeMonthlyFactBuilder product(DimProduct product) {
            this.product = product;
            return this;
        }

        public ImportTradeMonthlyFactBuilder quantityKg(Double quantityKg) {
            this.quantityKg = quantityKg;
            return this;
        }

        public ImportTradeMonthlyFactBuilder valueUsd(Double valueUsd) {
            this.valueUsd = valueUsd;
            return this;
        }

        public ImportTradeMonthlyFactBuilder sourceName(String sourceName) {
            this.sourceName = sourceName;
            return this;
        }

        public ImportTradeMonthlyFactBuilder qualityFlags(String qualityFlags) {
            this.qualityFlags = qualityFlags;
            return this;
        }

        public ImportTradeMonthlyFactBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ImportTradeMonthlyFact build() {
            return new ImportTradeMonthlyFact(factId, month, importer, exporter, hsCode, product, quantityKg, valueUsd,
                    sourceName, qualityFlags, createdAt);
        }
    }
}
