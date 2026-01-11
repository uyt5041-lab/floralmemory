

* Java 21
* Spring Boot 3.x
* package base: `com.bit.floralmemory`
* JPA 

---

## 0) 패키지/폴더 구조

```
src/main/java/com/bit/floralmemory/
  config/
  controller/
    ingestion/
    data/
    ontology/
    forecast/
    ordering/
    reports/
  dto/
    common/
    ingestion/
    data/
    ontology/
    forecast/
    ordering/
    reports/
  entity/
  repository/
  service/
  util/
```

---

## 1) 공통 DTO (Response Envelope)

### `src/main/java/com/bit/floralmemory/dto/common/ApiResponse.java`

```java
package com.bit.floralmemory.dto.common;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder().success(true).data(data).error(null).build();
    }

    public static <T> ApiResponse<T> fail(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .error(new ApiError(code, message))
                .build();
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class ApiError {
        private String code;
        private String message;
    }
}
```

---

## 2) Entity 스켈레톤 (DB_SCHEMA.md 기반)

> 날짜(month)는 `LocalDate`로 받고 **항상 day=01** 규칙을 서비스에서 보장.

### `src/main/java/com/bit/floralmemory/entity/DimCountry.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dim_country")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DimCountry {
    @Id
    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Column(name = "country_name", nullable = false)
    private String countryName;
}
```

### `src/main/java/com/bit/floralmemory/entity/DimProduct.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dim_product")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DimProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_slug", nullable = false, unique = true)
    private String productSlug;
}
```

### `src/main/java/com/bit/floralmemory/entity/DimHsCode.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dim_hs_code")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DimHsCode {
    @Id
    @Column(name = "hs_code", length = 10)
    private String hsCode;

    @Column(name = "hs_desc")
    private String hsDesc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_hs_code")
    private DimHsCode parent;
}
```

### `src/main/java/com/bit/floralmemory/entity/IngestionJob.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "ingestion_job")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class IngestionJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingestion_id")
    private Long ingestionId;

    @Column(name = "source_name", nullable = false)
    private String sourceName; // KCS, KOSIS, UN_COMTRADE

    @Column(name = "status", nullable = false)
    private String status; // RUNNING, SUCCESS, FAILED

    @Column(name = "requested_from", nullable = false)
    private LocalDate requestedFrom;

    @Column(name = "requested_to", nullable = false)
    private LocalDate requestedTo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "params_json", nullable = false, columnDefinition = "jsonb")
    private String paramsJson;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "started_at", nullable = false)
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;
}
```

### `src/main/java/com/bit/floralmemory/entity/ImportTradeMonthlyRaw.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "import_trade_monthly_raw")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ImportTradeMonthlyRaw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raw_id")
    private Long rawId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingestion_id", nullable = false)
    private IngestionJob ingestionJob;

    @Column(name = "month", nullable = false)
    private LocalDate month;

    @Column(name = "reporter_code", nullable = false, length = 3)
    private String reporterCode;

    @Column(name = "partner_code", nullable = false, length = 3)
    private String partnerCode;

    @Column(name = "hs_code_raw", nullable = false)
    private String hsCodeRaw;

    @Column(name = "quantity_kg_raw")
    private Double quantityKgRaw;

    @Column(name = "value_usd_raw")
    private Double valueUsdRaw;

    @Column(name = "unit_raw")
    private String unitRaw;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_json", columnDefinition = "jsonb")
    private String payloadJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
```

### `src/main/java/com/bit/floralmemory/entity/ImportTradeMonthlyFact.java`

```java
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
```

### `src/main/java/com/bit/floralmemory/entity/CalendarEvent.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "calendar_event", uniqueConstraints = @UniqueConstraint(columnNames = {"event_date","event_code"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_code", nullable = false)
    private String eventCode;

    @Column(name = "intensity", nullable = false)
    private Short intensity;

    @Column(name = "notes")
    private String notes;
}
```

### `src/main/java/com/bit/floralmemory/entity/CalendarEventMonthly.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "calendar_event_monthly")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CalendarEventMonthly {
    @Id
    @Column(name = "month")
    private LocalDate month;

    @Column(name = "event_score", nullable = false)
    private Double eventScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_detail", nullable = false, columnDefinition = "jsonb")
    private String eventDetail;
}
```

### `src/main/java/com/bit/floralmemory/entity/OntologyMappingHsProduct.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "ontology_mapping_hs_product",
       uniqueConstraints = @UniqueConstraint(columnNames = {"hs_code","product_id","source"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OntologyMappingHsProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long mappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hs_code", nullable = false)
    private DimHsCode hsCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private DimProduct product;

    @Column(name = "confidence", nullable = false)
    private Double confidence;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
```

### `src/main/java/com/bit/floralmemory/entity/ModelRun.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "model_run")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ModelRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "run_type", nullable = false)
    private String runType; // TRAIN, PREDICT, BACKTEST

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scope_json", nullable = false, columnDefinition = "jsonb")
    private String scopeJson;

    @Column(name = "train_start", nullable = false)
    private LocalDate trainStart;

    @Column(name = "train_end", nullable = false)
    private LocalDate trainEnd;

    @Column(name = "forecast_start", nullable = false)
    private LocalDate forecastStart;

    @Column(name = "forecast_end", nullable = false)
    private LocalDate forecastEnd;

    @Column(name = "granularity", nullable = false)
    private String granularity; // MONTH

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
```

### `src/main/java/com/bit/floralmemory/entity/ForecastResult.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "forecast_result",
       uniqueConstraints = @UniqueConstraint(columnNames = {"run_id","model_name","month"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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
    private String modelFamily; // SHORT, MID, LONG

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
```

### `src/main/java/com/bit/floralmemory/entity/ForecastEnsemble.java`

```java
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
```

### `src/main/java/com/bit/floralmemory/entity/CostAssumption.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "cost_assumption", uniqueConstraints = @UniqueConstraint(columnNames = {"run_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CostAssumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cost_id")
    private Long costId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private ModelRun run;

    @Column(name = "co_unit", nullable = false)
    private Double coUnit;

    @Column(name = "cu_unit", nullable = false)
    private Double cuUnit;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "rationale")
    private String rationale;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
```

### `src/main/java/com/bit/floralmemory/entity/OrderingRun.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "ordering_run", uniqueConstraints = @UniqueConstraint(columnNames = {"run_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderingRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordering_run_id")
    private Long orderingRunId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private ModelRun run;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "service_level_policy", nullable = false, columnDefinition = "jsonb")
    private String serviceLevelPolicy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "safety_rules", nullable = false, columnDefinition = "jsonb")
    private String safetyRules;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
```

### `src/main/java/com/bit/floralmemory/entity/OrderRecommendation.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "order_recommendation",
       uniqueConstraints = @UniqueConstraint(columnNames = {"ordering_run_id","month"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_run_id", nullable = false)
    private OrderingRun orderingRun;

    @Column(name = "month", nullable = false)
    private LocalDate month;

    @Column(name = "yhat", nullable = false)
    private Double yhat;

    @Column(name = "sigma", nullable = false)
    private Double sigma;

    @Column(name = "service_level", nullable = false)
    private Double serviceLevel;

    @Column(name = "z_value", nullable = false)
    private Double zValue;

    @Column(name = "on_hand", nullable = false)
    private Double onHand;

    @Column(name = "lead_time_months", nullable = false)
    private Short leadTimeMonths;

    @Column(name = "order_qty", nullable = false)
    private Double orderQty;

    @Column(name = "expected_waste_cost", nullable = false)
    private Double expectedWasteCost;

    @Column(name = "expected_stockout_loss", nullable = false)
    private Double expectedStockoutLoss;

    @Column(name = "expected_total_loss", nullable = false)
    private Double expectedTotalLoss;

    @Column(name = "explanation", nullable = false)
    private String explanation;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
```

### `src/main/java/com/bit/floralmemory/entity/ModelMetric.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "model_metric", uniqueConstraints = @UniqueConstraint(columnNames = {"run_id","model_name"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ModelMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metric_id")
    private Long metricId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    private ModelRun run;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "wape")
    private Double wape;

    @Column(name = "mape")
    private Double mape;

    @Column(name = "expected_total_loss")
    private Double expectedTotalLoss;

    @Column(name = "expected_waste_cost")
    private Double expectedWasteCost;

    @Column(name = "expected_stockout_loss")
    private Double expectedStockoutLoss;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notes", nullable = false, columnDefinition = "jsonb")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
```

### `src/main/java/com/bit/floralmemory/entity/ContextFeatureMonthly.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "context_feature_monthly")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ContextFeatureMonthly {
    // Composite Key would be better, but for skeleton let's use ID class or just separate fields
    // Spec says: PRIMARY KEY (scope_hash, month)
    
    @EmbeddedId
    private ContextFeatureId id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features_json", nullable = false, columnDefinition = "jsonb")
    private String featuresJson;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Embeddable
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @EqualsAndHashCode
    public static class ContextFeatureId implements java.io.Serializable {
        @Column(name = "scope_hash", nullable = false)
        private String scopeHash;

        @Column(name = "month", nullable = false)
        private LocalDate month;
    }
}
```

### `src/main/java/com/bit/floralmemory/entity/PolicySweepRun.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "policy_sweep_run")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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
```

### `src/main/java/com/bit/floralmemory/entity/PolicySweepResult.java`

```java
package com.bit.floralmemory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.*;

@Entity
@Table(name = "policy_sweep_result")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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
    private Boolean isBest;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
```
```

---

## 3) Repository 스켈레톤 (JPA)

### `src/main/java/com/bit/floralmemory/repository/IngestionJobRepository.java`

```java
package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.IngestionJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionJobRepository extends JpaRepository<IngestionJob, Long> {
}
```

### `src/main/java/com/bit/floralmemory/repository/ImportTradeMonthlyFactRepository.java`

```java
package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.ImportTradeMonthlyFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ImportTradeMonthlyFactRepository extends JpaRepository<ImportTradeMonthlyFact, Long> {

    @Query("""
        select f from ImportTradeMonthlyFact f
        where f.month between :from and :to
          and f.importer.countryCode = :importer
          and f.exporter.countryCode = :exporter
          and f.product.productSlug = :productSlug
        order by f.month asc
    """)
    List<ImportTradeMonthlyFact> findSeries(LocalDate from, LocalDate to, String importer, String exporter, String productSlug);
}
```

### `src/main/java/com/bit/floralmemory/repository/ForecastEnsembleRepository.java`

```java
package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.ForecastEnsemble;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForecastEnsembleRepository extends JpaRepository<ForecastEnsemble, Long> {
    List<ForecastEnsemble> findByRun_RunIdOrderByMonthAsc(Long runId);
}
```

(나머지도 동일 패턴으로 필요시 추가: ForecastResultRepository, OrderingRunRepository, OrderRecommendationRepository, ModelMetricRepository, OntologyMappingRepository)

---

## 4) DTO 스켈레톤 (API_SPEC.md 기반)

### Ingestion DTO

`src/main/java/com/bit/floralmemory/dto/ingestion/IngestionRunRequest.java`

```java
package com.bit.floralmemory.dto.ingestion;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IngestionRunRequest {
    private String source;        // KCS, KOSIS, UN_COMTRADE
    private LocalDate fromMonth;
    private LocalDate toMonth;
    private Scope scope;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Scope {
        private String importer;  // KOR
        private String exporter;  // COL
        private List<String> hsCodes;
    }
}
```

`src/main/java/com/bit/floralmemory/dto/ingestion/IngestionRunResponse.java`

```java
package com.bit.floralmemory.dto.ingestion;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IngestionRunResponse {
    private Long ingestionId;
    private String status;
}
```

---

### Data DTO

`src/main/java/com/bit/floralmemory/dto/data/ImportSeriesResponse.java`

```java
package com.bit.floralmemory.dto.data;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ImportSeriesResponse {
    private List<Row> series;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Row {
        private LocalDate month;
        private Double quantityKg;
        private Double valueUsd;
        private String hsCode;
    }
}
```

---

### Ontology DTO

`src/main/java/com/bit/floralmemory/dto/ontology/UpsertMappingRequest.java`

```java
package com.bit.floralmemory.dto.ontology;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpsertMappingRequest {
    private String hsCode;
    private String productSlug;
    private Double confidence;
    private String source; // manual, sparql, rule
}
```

---

### Forecast DTO

`src/main/java/com/bit/floralmemory/dto/forecast/ForecastRunRequest.java`

```java
package com.bit.floralmemory.dto.forecast;

import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ForecastRunRequest {
    private Scope scope;
    private LocalDate trainStart;
    private LocalDate trainEnd;
    private LocalDate forecastStart;
    private LocalDate forecastEnd;
    private List<String> targets; // ["quantityKg"]

    private Models models;
    private Ensemble ensemble;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Scope {
        private String importer;
        private String exporter;
        private String productSlug;
        private List<String> hsCodes;
        private String granularity; // MONTH
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Models {
        private List<String> shortModels;
        private List<String> midModels;
        private List<String> longModels;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Ensemble {
        private boolean enabled;
        private Map<String, Map<String, Double>> weights; 
        // horizonLe3m -> {short:0.4, mid:0.4, long:0.2}
    }
}
```

`src/main/java/com/bit/floralmemory/dto/forecast/ForecastRunResponse.java`

```java
package com.bit.floralmemory.dto.forecast;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ForecastRunResponse {
    private Long runId;
}
```

`src/main/java/com/bit/floralmemory/dto/forecast/ForecastResultsResponse.java`

```java
package com.bit.floralmemory.dto.forecast;

import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ForecastResultsResponse {
    private Long runId;
    private String type; // SHORT|MID|LONG|ENSEMBLE
    private List<Row> series;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Row {
        private LocalDate month;
        private Double yhat;
        private Double sigma;
        private Map<String, Double> weights; // ensemble only
    }
}
```

---

### Ordering DTO

`src/main/java/com/bit/floralmemory/dto/ordering/OrderingRunRequest.java`

```java
package com.bit.floralmemory.dto.ordering;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderingRunRequest {
    private Long runId;
    private CostAssumption costAssumption;
    private Policy policy;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CostAssumption {
        private Double coUnit;
        private Double cuUnit;
        private String currency;
        private String rationale;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Policy {
        private Map<String, Object> serviceLevel;
        private Map<String, Object> safetyRules;
        private Map<String, Object> inventory;
    }
}
```

`src/main/java/com/bit/floralmemory/dto/ordering/OrderingRunResponse.java`

```java
package com.bit.floralmemory.dto.ordering;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderingRunResponse {
    private Long orderingRunId;
}
```

`src/main/java/com/bit/floralmemory/dto/ordering/OrderingResultsResponse.java`

```java
package com.bit.floralmemory.dto.ordering;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderingResultsResponse {
    private Long orderingRunId;
    private List<Row> series;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Row {
        private LocalDate month;
        private Double yhat;
        private Double sigma;
        private Double serviceLevel;
        private Double zValue;
        private Double orderQty;
        private Double expectedWasteCost;
        private Double expectedStockoutLoss;
        private Double expectedTotalLoss;
        private String explanation;
    }
}
```

---

### Reports DTO

`src/main/java/com/bit/floralmemory/dto/reports/ModelComparisonResponse.java`

```java
package com.bit.floralmemory.dto.reports;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModelComparisonResponse {
    private Long runId;
    private List<Row> rows;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Row {
        private String modelName;
        private Double wape;
        private Double expectedTotalLoss;
    }
}
```

---

## 5) Controller 스켈레톤 (경로 고정)

### `src/main/java/com/bit/floralmemory/controller/ingestion/IngestionController.java`

```java
package com.bit.floralmemory.controller.ingestion;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.ingestion.IngestionRunRequest;
import com.bit.floralmemory.dto.ingestion.IngestionRunResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
public class IngestionController {

    // private final IngestionService ingestionService;

    @PostMapping("/run")
    public ApiResponse<IngestionRunResponse> run(@RequestBody IngestionRunRequest req) {
        // TODO: validate + start ingestion job
        return ApiResponse.ok(IngestionRunResponse.builder()
                .ingestionId(0L)
                .status("RUNNING")
                .build());
    }

    @GetMapping("/{ingestionId}")
    public ApiResponse<Object> status(@PathVariable Long ingestionId) {
        // TODO: return ingestion job status
        return ApiResponse.ok(null);
    }
}
```

### `src/main/java/com/bit/floralmemory/controller/data/DataController.java`

```java
package com.bit.floralmemory.controller.data;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.data.ImportSeriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    // private final DataService dataService;

    @GetMapping("/imports")
    public ApiResponse<ImportSeriesResponse> imports(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam String importer,
            @RequestParam String exporter,
            @RequestParam String productSlug
    ) {
        // TODO: fetch from import_trade_monthly_fact
        return ApiResponse.ok(ImportSeriesResponse.builder().series(java.util.List.of()).build());
    }
}
```

### `src/main/java/com/bit/floralmemory/controller/ontology/OntologyController.java`

```java
package com.bit.floralmemory.controller.ontology;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.ontology.UpsertMappingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ontology")
@RequiredArgsConstructor
public class OntologyController {

    // private final OntologyService ontologyService;

    @GetMapping("/mappings")
    public ApiResponse<Object> list(@RequestParam String productSlug) {
        // TODO
        return ApiResponse.ok(null);
    }

    @PostMapping("/mappings")
    public ApiResponse<Object> upsert(@RequestBody UpsertMappingRequest req) {
        // TODO
        return ApiResponse.ok(java.util.Map.of("updated", true));
    }
}
```

### `src/main/java/com/bit/floralmemory/controller/forecast/ForecastController.java`

```java
package com.bit.floralmemory.controller.forecast;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.forecast.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {

    // private final ForecastService forecastService;

    @PostMapping("/run")
    public ApiResponse<ForecastRunResponse> run(@RequestBody ForecastRunRequest req) {
        // TODO: create model_run + call model-service + persist forecast_result + ensemble
        return ApiResponse.ok(ForecastRunResponse.builder().runId(0L).build());
    }

    @GetMapping("/results")
    public ApiResponse<ForecastResultsResponse> results(@RequestParam Long runId, @RequestParam String type) {
        // TODO: load from forecast_result or forecast_ensemble
        return ApiResponse.ok(ForecastResultsResponse.builder().runId(runId).type(type).series(java.util.List.of()).build());
    }
}
```

### `src/main/java/com/bit/floralmemory/controller/ordering/OrderingController.java`

```java
package com.bit.floralmemory.controller.ordering;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.ordering.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordering")
@RequiredArgsConstructor
public class OrderingController {

    // private final OrderingService orderingService;

    @PostMapping("/run")
    public ApiResponse<OrderingRunResponse> run(@RequestBody OrderingRunRequest req) {
        // TODO: create ordering_run + cost_assumption + order_recommendation
        return ApiResponse.ok(OrderingRunResponse.builder().orderingRunId(0L).build());
    }

    @GetMapping("/results")
    public ApiResponse<OrderingResultsResponse> results(@RequestParam Long orderingRunId) {
        // TODO
        return ApiResponse.ok(OrderingResultsResponse.builder().orderingRunId(orderingRunId).series(java.util.List.of()).build());
    }
}
```

### `src/main/java/com/bit/floralmemory/controller/reports/ReportsController.java`

```java
package com.bit.floralmemory.controller.reports;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.reports.ModelComparisonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {

    // private final ReportsService reportsService;

    @GetMapping("/models")
    public ApiResponse<ModelComparisonResponse> modelComparison(@RequestParam Long runId) {
        // TODO: return model_metric rows for runId
        return ApiResponse.ok(ModelComparisonResponse.builder().runId(runId).rows(java.util.List.of()).build());
    }
}
```

---

## 6) Service 스켈레톤(최소)

원하면 다음 단계에서 Service/Client까지 “진짜 동작”하게 이어 붙일 수 있는데, 일단 스켈레톤만:

### `src/main/java/com/bit/floralmemory/service/ForecastService.java`

```java
package com.bit.floralmemory.service;

import com.bit.floralmemory.dto.forecast.ForecastRunRequest;

public interface ForecastService {
    Long runForecast(ForecastRunRequest req);
}
```

### `src/main/java/com/bit/floralmemory/service/OrderingService.java`

```java
package com.bit.floralmemory.service;

import com.bit.floralmemory.dto.ordering.OrderingRunRequest;

public interface OrderingService {
    Long runOrdering(OrderingRunRequest req);
}
```

---

