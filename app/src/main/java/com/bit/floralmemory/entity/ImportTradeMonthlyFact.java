package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(
        name = "import_trade_monthly_fact",
        uniqueConstraints = @UniqueConstraint(columnNames = {"month","importer_code","exporter_code","hs_code","source_name"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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
}
