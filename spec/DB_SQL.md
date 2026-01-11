좋아. **1번(“DB 스키마 + API Request/Response 명세”)**부터 못 박자.
아래는 그대로 `docs/DB_SCHEMA.md`, `docs/API_SPEC.md`로 저장하면 되는 **완성본**이야.

---

## docs/DB_SCHEMA.md

````md
# DB_SCHEMA.md
# PostgreSQL Schema (Pilot: Colombia Carnation, Monthly, 2022–2025)

## 0) Design Goals
- Monthly time series (YYYY-MM)
- Store raw ingestion + normalized facts
- Store model runs, forecasts (yhat + sigma), ensembles
- Store ordering decisions + cost assumptions + explanation logs
- Store ontology mappings (HS ↔ Product) + event calendar

---

## 1) Naming & Types
- month is stored as DATE with day=01 (e.g., 2025-01-01)
- IDs are BIGSERIAL unless explicitly string keys
- All "run" tables include created_at + run_id for traceability

---

## 1.3 Schema Management (Flyway / JPA Auto Toggle)

We support two modes:
- MODE_A (Fast): JPA ddl-auto=create/update (local-only quick start)
- MODE_B (Controlled): Flyway migrations (dev/stage/prod)

Rule:
- Never use ddl-auto=update in prod.
- Flyway is the source of truth when enabled.

---

## 2) Core Dimension Tables

### 2.1 dim_country
Stores ISO metadata for importer/exporter.
```sql
CREATE TABLE dim_country (
  country_code VARCHAR(3) PRIMARY KEY,   -- ISO alpha-3: KOR, COL
  country_name TEXT NOT NULL
);
````

### 2.2 dim_product

Pilot uses "Carnation" but allows expansion.

```sql
CREATE TABLE dim_product (
  product_id BIGSERIAL PRIMARY KEY,
  product_name TEXT NOT NULL,            -- "Carnation"
  product_slug TEXT UNIQUE NOT NULL      -- "carnation"
);
```

### 2.3 dim_hs_code

HS code catalog (can be partial).

```sql
CREATE TABLE dim_hs_code (
  hs_code VARCHAR(10) PRIMARY KEY,       -- "060310", "0603.10" normalized to digits
  hs_desc TEXT,
  parent_hs_code VARCHAR(10) NULL REFERENCES dim_hs_code(hs_code)
);
```

---

## 3) Ingestion & Normalized Facts

### 3.1 ingestion_job

Tracks ingestion executions.

```sql
CREATE TABLE ingestion_job (
  ingestion_id BIGSERIAL PRIMARY KEY,
  source_name TEXT NOT NULL,             -- "KCS", "KOSIS", "UN_COMTRADE"
  status TEXT NOT NULL,                  -- "RUNNING","SUCCESS","FAILED"
  requested_from DATE NOT NULL,
  requested_to DATE NOT NULL,
  params_json JSONB NOT NULL,
  error_message TEXT,
  started_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  finished_at TIMESTAMPTZ
);
```

### 3.2 import_trade_monthly_raw

Raw records as received (do not edit after insert).

```sql
CREATE TABLE import_trade_monthly_raw (
  raw_id BIGSERIAL PRIMARY KEY,
  ingestion_id BIGINT NOT NULL REFERENCES ingestion_job(ingestion_id),
  month DATE NOT NULL,                   -- YYYY-MM-01
  reporter_code VARCHAR(3) NOT NULL,     -- importer: "KOR"
  partner_code VARCHAR(3) NOT NULL,      -- exporter: "COL"
  hs_code_raw TEXT NOT NULL,
  quantity_kg_raw NUMERIC,
  value_usd_raw NUMERIC,
  unit_raw TEXT,
  payload_json JSONB,                    -- original API payload fragment
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_import_raw_month ON import_trade_monthly_raw(month);
CREATE INDEX idx_import_raw_ingestion ON import_trade_monthly_raw(ingestion_id);
```

### 3.3 import_trade_monthly_fact

Normalized, deduplicated fact table (the main time series source).

```sql
CREATE TABLE import_trade_monthly_fact (
  fact_id BIGSERIAL PRIMARY KEY,
  month DATE NOT NULL,
  importer_code VARCHAR(3) NOT NULL REFERENCES dim_country(country_code),
  exporter_code VARCHAR(3) NOT NULL REFERENCES dim_country(country_code),
  hs_code VARCHAR(10) NOT NULL REFERENCES dim_hs_code(hs_code),
  product_id BIGINT NULL REFERENCES dim_product(product_id),  -- set via ontology mapping
  quantity_kg NUMERIC NOT NULL DEFAULT 0,
  value_usd NUMERIC NOT NULL DEFAULT 0,
  source_name TEXT NOT NULL,             -- canonical source used for this record
  quality_flags JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (month, importer_code, exporter_code, hs_code, source_name)
);

CREATE INDEX idx_import_fact_month ON import_trade_monthly_fact(month);
CREATE INDEX idx_import_fact_product ON import_trade_monthly_fact(product_id);
CREATE INDEX idx_import_fact_hs ON import_trade_monthly_fact(hs_code);
```

---

## 4) Event Calendar (Knowledge Injection)

### 4.1 calendar_event

Events used by Prophet and reporting.

```sql
CREATE TABLE calendar_event (
  event_id BIGSERIAL PRIMARY KEY,
  event_date DATE NOT NULL,              -- monthly pilot can still use exact date
  event_code TEXT NOT NULL,              -- "MOTHER_DAY", "GRADUATION", ...
  intensity SMALLINT NOT NULL DEFAULT 1, -- 1..3
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (event_date, event_code)
);

CREATE INDEX idx_event_date ON calendar_event(event_date);
```

### 4.2 calendar_event_monthly

Optional: precomputed monthly event features (for monthly models).

```sql
CREATE TABLE calendar_event_monthly (
  month DATE PRIMARY KEY,
  event_score NUMERIC NOT NULL DEFAULT 0, -- aggregated score
  event_detail JSONB NOT NULL DEFAULT '{}'::jsonb
);
```

---

### 4.3 context_feature_monthly (NEW)

Context features per scope+month (extensible exogenous signals).

```sql
CREATE TABLE context_feature_monthly (
  scope_hash TEXT NOT NULL,               -- hash(scope_json) for join convenience
  month DATE NOT NULL,                    -- YYYY-MM-01
  features_json JSONB NOT NULL,           -- {"event_score":0.7,"holiday":1,"fx_usdkrw":1320,...}
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (scope_hash, month)
);

CREATE INDEX idx_context_feature_month ON context_feature_monthly(month);
```

> Notes:
> - calendar_event / calendar_event_monthly are “human-editable” sources.
> - context_feature_monthly is the “model-ready” feature pack.
> - features_json must include at least: event_score (double).

---

## 5) Ontology / Mapping Layer (MVP)

### 5.1 ontology_mapping_hs_product

Maps HS codes to products (Carnation).

```sql
CREATE TABLE ontology_mapping_hs_product (
  mapping_id BIGSERIAL PRIMARY KEY,
  hs_code VARCHAR(10) NOT NULL REFERENCES dim_hs_code(hs_code),
  product_id BIGINT NOT NULL REFERENCES dim_product(product_id),
  confidence NUMERIC NOT NULL DEFAULT 1.0,
  source TEXT NOT NULL DEFAULT 'manual',  -- "manual","sparql","rule"
  valid_from DATE NULL,
  valid_to DATE NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (hs_code, product_id, source)
);
```

> Note: Triplestore is optional for pilot. This table is the MVP bridge.

---

## 6) Forecasting Artifacts

### 6.1 model_run

Tracks training/prediction runs.

```sql
CREATE TABLE model_run (
  run_id BIGSERIAL PRIMARY KEY,
  run_type TEXT NOT NULL,                -- "TRAIN","PREDICT","BACKTEST"
  scope_json JSONB NOT NULL,             -- {importer:"KOR", exporter:"COL", product:"carnation", hs_codes:[...]}
  train_start DATE NOT NULL,
  train_end DATE NOT NULL,
  forecast_start DATE NOT NULL,
  forecast_end DATE NOT NULL,
  granularity TEXT NOT NULL DEFAULT 'MONTH',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

### 6.2 forecast_result

Stores forecasts from each model (short/mid/long).

```sql
CREATE TABLE forecast_result (
  forecast_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  model_name TEXT NOT NULL,              -- "ShortHoltWinters","MidProphetEvents","LongSARIMA",...
  model_family TEXT NOT NULL,            -- "SHORT","MID","LONG"
  month DATE NOT NULL,
  yhat NUMERIC NOT NULL,
  sigma NUMERIC NOT NULL,
  extra_json JSONB NOT NULL DEFAULT '{}'::jsonb, -- intervals, components, etc.
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (run_id, model_name, month)
);

CREATE INDEX idx_forecast_run ON forecast_result(run_id);
CREATE INDEX idx_forecast_month ON forecast_result(month);
```

### 6.3 forecast_ensemble

Stores final ensemble per month.

```sql
CREATE TABLE forecast_ensemble (
  ensemble_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  month DATE NOT NULL,
  yhat NUMERIC NOT NULL,
  sigma NUMERIC NOT NULL,
  weights_json JSONB NOT NULL,           -- {"ShortHoltWinters":0.2, ...}
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (run_id, month)
);
```

---

## 7) Ordering / Decision Artifacts

### 7.1 cost_assumption

Stores Co/Cu assumptions used in a decision run.

```sql
CREATE TABLE cost_assumption (
  cost_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  co_unit NUMERIC NOT NULL,              -- waste unit cost
  cu_unit NUMERIC NOT NULL,              -- stockout unit loss
  currency TEXT NOT NULL DEFAULT 'USD',
  rationale TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (run_id)
);
```

### 7.2 ordering_run

Tracks ordering execution (may be same as model_run, but kept separate for clarity).

```sql
CREATE TABLE ordering_run (
  ordering_run_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  service_level_policy JSONB NOT NULL,   -- grade rules, clips, etc.
  safety_rules JSONB NOT NULL,           -- p90 cap, etc.
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (run_id)
);
```

### 7.3 order_recommendation

Final output of the system.

```sql
CREATE TABLE order_recommendation (
  order_id BIGSERIAL PRIMARY KEY,
  ordering_run_id BIGINT NOT NULL REFERENCES ordering_run(ordering_run_id),
  month DATE NOT NULL,
  yhat NUMERIC NOT NULL,
  sigma NUMERIC NOT NULL,
  service_level NUMERIC NOT NULL,
  z_value NUMERIC NOT NULL,
  on_hand NUMERIC NOT NULL DEFAULT 0,
  lead_time_months SMALLINT NOT NULL DEFAULT 0,
  order_qty NUMERIC NOT NULL,
  expected_waste_cost NUMERIC NOT NULL,
  expected_stockout_loss NUMERIC NOT NULL,
  expected_total_loss NUMERIC NOT NULL,
  explanation TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (ordering_run_id, month)
);

CREATE INDEX idx_order_month ON order_recommendation(month);
```

---

### 7.4 policy_sweep_run (NEW)

Policy sweep executions to find optimal Co/Cu.

```sql
CREATE TABLE policy_sweep_run (
  sweep_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  objective TEXT NOT NULL DEFAULT 'MIN_EXPECTED_TOTAL_LOSS',
  grid_json JSONB NOT NULL,               -- {"co":[...],"cu":[...],"sigmaInflation":[...],...}
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

### 7.5 policy_sweep_result (NEW)

Sweep trial results.

```sql
CREATE TABLE policy_sweep_result (
  sweep_result_id BIGSERIAL PRIMARY KEY,
  sweep_id BIGINT NOT NULL REFERENCES policy_sweep_run(sweep_id),
  co_unit NUMERIC NOT NULL,
  cu_unit NUMERIC NOT NULL,
  service_level NUMERIC NOT NULL,
  z_value NUMERIC NOT NULL,
  sigma_inflation NUMERIC NOT NULL DEFAULT 1.0,   -- 1.0 means no inflation
  yhat_shrink NUMERIC NOT NULL DEFAULT 0.0,       -- 0.0 means no shrink
  expected_total_loss NUMERIC,
  expected_waste_cost NUMERIC,
  expected_stockout_loss NUMERIC,
  constraints_json JSONB NOT NULL DEFAULT '{}'::jsonb,
  is_best BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_sweep_result_best ON policy_sweep_result(sweep_id, is_best);
```

---

## 8) Evaluation / Reporting

### 8.1 model_metric

Stores accuracy + cost-based metrics per model run.

```sql
CREATE TABLE model_metric (
  metric_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  model_name TEXT NOT NULL,              -- per-model or "ENSEMBLE"
  wape NUMERIC,
  mape NUMERIC,
  expected_total_loss NUMERIC,
  expected_waste_cost NUMERIC,
  expected_stockout_loss NUMERIC,
  notes JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (run_id, model_name)
);
```

---

## 9) Minimum Seed Data (Pilot)

* dim_country: KOR, COL
* dim_product: Carnation
* dim_hs_code: 0603... known subset + parents if available
* ontology_mapping_hs_product: map HS codes used to Carnation (manual first)

````

---

## docs/API_SPEC.md

```md
# API_SPEC.md
# Spring Boot API (Pilot) + Model Service Contract

Base URL (Backend): /api
All dates:
- month fields use YYYY-MM-01 in JSON (ISO date)

---

## 1) Common Conventions

### 1.1 Response envelope
All endpoints return:
```json
{
  "success": true,
  "data": {},
  "error": null
}
````

On error:

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "..."
  }
}
```

### 1.2 Scope object (reused)

```json
{
  "importer": "KOR",
  "exporter": "COL",
  "productSlug": "carnation",
  "hsCodes": ["060310"],
  "granularity": "MONTH"
}
```

---

## 2) Ingestion APIs

### 2.1 Run ingestion

POST /api/ingestion/run

Request:

```json
{
  "source": "KCS",
  "fromMonth": "2022-01-01",
  "toMonth": "2025-12-01",
  "scope": {
    "importer": "KOR",
    "exporter": "COL",
    "hsCodes": ["060310"]
  }
}
```

Response:

```json
{
  "success": true,
  "data": { "ingestionId": 101, "status": "RUNNING" },
  "error": null
}
```

### 2.2 Get ingestion status

GET /api/ingestion/{ingestionId}

Response:

```json
{
  "success": true,
  "data": {
    "ingestionId": 101,
    "source": "KCS",
    "status": "SUCCESS",
    "requestedFrom": "2022-01-01",
    "requestedTo": "2025-12-01",
    "startedAt": "...",
    "finishedAt": "..."
  },
  "error": null
}
```

---

## 2.3 Context APIs (NEW)

### 2.3.1 Upsert calendar events (manual or batch)
POST /api/context/events

Request:
```json
{
  "events": [
    { "date": "2025-02-14", "code": "VALENTINES", "intensity": 5, "notes": "..." },
    { "date": "2025-05-08", "code": "PARENTS_DAY", "intensity": 4, "notes": "..." }
  ]
}
```

Response:
```json
{ "success": true, "data": { "upserted": 2 }, "error": null }
```

### 2.3.2 Rebuild monthly features for a scope+range
POST /api/context/rebuild

Request:
```json
{
  "scope": { ... },
  "fromMonth": "2022-01-01",
  "toMonth": "2025-12-01"
}
```

Response:
```json
{ "success": true, "data": { "monthsBuilt": 48 }, "error": null }
```

---

## 3) Data Query APIs

### 3.1 Get normalized monthly imports

GET /api/data/imports?from=2022-01-01&to=2025-12-01&importer=KOR&exporter=COL&productSlug=carnation

Response:

```json
{
  "success": true,
  "data": {
    "series": [
      { "month": "2022-01-01", "quantityKg": 1234.5, "valueUsd": 45678.9, "hsCode": "060310" }
    ]
  },
  "error": null
}
```

---

## 4) Ontology Mapping APIs (MVP)

### 4.1 List HS ↔ Product mappings

GET /api/ontology/mappings?productSlug=carnation

Response:

```json
{
  "success": true,
  "data": {
    "mappings": [
      { "hsCode": "060310", "productSlug": "carnation", "confidence": 1.0, "source": "manual" }
    ]
  },
  "error": null
}
```

### 4.2 Upsert mapping

POST /api/ontology/mappings

Request:

```json
{
  "hsCode": "060310",
  "productSlug": "carnation",
  "confidence": 1.0,
  "source": "manual"
}
```

Response:

```json
{
  "success": true,
  "data": { "updated": true },
  "error": null
}
```

---

## 5) Forecast Orchestration APIs (Backend)

Forecast models are grouped:
- SHORT: Holt-Winters / ETS baseline
- MID: Prophet (with events)
- LONG: SARIMA (seasonality + long trend)

The backend persists per-model forecasts into forecast_result (yhat + sigma),
and the final ensemble into forecast_ensemble (weights_json).

### 5.1 Run forecast (Short/Mid/Long + Ensemble)

POST /api/forecast/run

Request:

```json
{
  "scope": {
    "importer": "KOR",
    "exporter": "COL",
    "productSlug": "carnation",
    "hsCodes": ["060310"],
    "granularity": "MONTH"
  },
  "trainStart": "2022-01-01",
  "trainEnd": "2024-12-01",
  "forecastStart": "2025-01-01",
  "forecastEnd": "2025-12-01",
  "targets": ["quantityKg"],
  "models": {
    "short": ["ShortHoltWinters"],
    "mid": ["MidProphetEvents"],
    "long": ["LongSARIMA"]
  },
  "ensemble": {
    "enabled": true,
    "weights": {
      "horizonLe3m": {"short": 0.4, "mid": 0.4, "long": 0.2},
      "horizon4to6m": {"short": 0.2, "mid": 0.4, "long": 0.4},
      "horizon7to12m": {"short": 0.1, "mid": 0.3, "long": 0.6}
    }
  }
}
```

Response:

```json
{
  "success": true,
  "data": { "runId": 2001 },
  "error": null
}
```

### 5.2 Get forecast results by run

GET /api/forecast/results?runId=2001&type=ENSEMBLE

* type: SHORT|MID|LONG|ENSEMBLE

Response:

```json
{
  "success": true,
  "data": {
    "runId": 2001,
    "type": "ENSEMBLE",
    "series": [
      { "month": "2025-01-01", "yhat": 1200.0, "sigma": 180.0, "weights": {"ShortHoltWinters":0.1,"MidProphetEvents":0.3,"LongSARIMA":0.6} }
    ]
  },
  "error": null
}
```

---

## 6) Ordering APIs

### 6.1 Run ordering decision

POST /api/ordering/run

Request:

```json
{
  "runId": 2001,
  "costAssumption": {
    "coUnit": 1.0,
    "cuUnit": 1.5,
    "currency": "USD",
    "rationale": "Pilot defaults"
  },
  "policy": {
    "serviceLevel": {
      "method": "CuOverCuPlusCo",
      "gradeClips": {
        "A": {"min": 0.90},
        "B": {"min": 0.75, "max": 0.90},
        "C": {"min": 0.60, "max": 0.80}
      }
    },
    "safetyRules": {
      "p90Cap": {"enabled": true, "lookbackMonths": 24}
    },
    "inventory": {
      "onHandDefault": 0,
      "leadTimeMonthsDefault": 0
    }
  }
}
```

Response:

```json
{
  "success": true,
  "data": { "orderingRunId": 3001 },
  "error": null
}
```

### 6.2 Get ordering results

GET /api/ordering/results?orderingRunId=3001

Response:

```json
{
  "success": true,
  "data": {
    "orderingRunId": 3001,
    "series": [
      {
        "month": "2025-01-01",
        "yhat": 1200.0,
        "sigma": 180.0,
        "serviceLevel": 0.88,
        "zValue": 1.175,
        "orderQty": 1411.5,
        "expectedWasteCost": 50.0,
        "expectedStockoutLoss": 120.0,
        "expectedTotalLoss": 170.0,
        "explanation": "12m horizon ensemble, SL from Cu/Co, capped by p90"
      }
    ]
  },
  "error": null
}
```

---

### 6.3 Ordering Policy Sweep API (NEW)

POST /api/ordering/policy-sweep

Goal:
- choose Co/Cu + conservatism knobs (sigma inflation, yhat shrink)
- minimize expected_total_loss (primary KPI)

Request:
```json
{
  "runId": 123,
  "grid": {
    "coUnit": [0.2, 0.4, 0.8],
    "cuUnit": [0.5, 1.0, 2.0],
    "sigmaInflation": [1.0, 1.2, 1.5],
    "yhatShrink": [0.0, 0.05, 0.10]
  },
  "constraints": {
    "minServiceLevel": 0.6,
    "maxOrderCapP90": true
  }
}
```

Response:
```json
{
  "success": true,
  "data": {
    "sweepId": 77,
    "best": {
      "coUnit": 0.4,
      "cuUnit": 1.0,
      "serviceLevel": 0.714,
      "zValue": 0.565,
      "sigmaInflation": 1.2,
      "yhatShrink": 0.05,
      "expectedTotalLoss": 12345.67
    }
  },
  "error": null
}
```

---

## 7) Reports APIs

### 7.1 Model comparison

GET /api/reports/models?runId=2001

Response:

```json
{
  "success": true,
  "data": {
    "runId": 2001,
    "rows": [
      { "modelName": "ShortHoltWinters", "wape": 0.18, "expectedTotalLoss": 1234.0 },
      { "modelName": "MidProphetEvents", "wape": 0.15, "expectedTotalLoss": 1100.0 },
      { "modelName": "LongSARIMA", "wape": 0.16, "expectedTotalLoss": 1150.0 },
      { "modelName": "ENSEMBLE", "wape": 0.14, "expectedTotalLoss": 980.0 }
    ]
  },
  "error": null
}
```

---

## 8) Python Model Service Contract (Internal)

Backend calls the model service; not exposed to end-users.

Base URL: [http://model-service:8000](http://model-service:8000)

### 8.1 Train+Predict (single call)

POST /forecast

Request:

```json
{
  "model": "LongSARIMA",
  "train": [
    {"ds":"2022-01-01","y":1000.0,"event_score":0.0},
    {"ds":"2022-02-01","y":1100.0,"event_score":0.0}
  ],
  "future": [
    {"ds":"2025-01-01","event_score":1.0},
    {"ds":"2025-02-01","event_score":0.0}
  ],
  "seasonal_period": 12
}
```

Response:

```json
{
  "model": "LongSARIMA",
  "pred": [
    {"ds":"2025-01-01","yhat":1200.0,"sigma":180.0},
    {"ds":"2025-02-01","yhat":1180.0,"sigma":175.0}
  ],
  "meta": {
    "train_start":"2022-01-01",
    "train_end":"2024-12-01",
    "sigma_method":"forecast_variance"
  }
}
```

Failure rules:

* If model fails, return HTTP 422 with error detail.
* Backend must set that model's weight to 0 and re-normalize ensemble weights.

```

---

## “이 스펙에서 추가로 못 박은 중요한 포인트” (짧게)
- **월별(date=YYYY-MM-01)**로 통일해서 모델/DB/API가 흔들리지 않음
- `run_id` 중심으로 **재현성과 추적성** 확보
- `forecast_result`(모델별)와 `forecast_ensemble`(최종)을 분리
- ordering에서 **설명(explanation)**이 1급 시민으로 저장됨

---

다음 바로 이어서 할 수 있는 건 2개야:
1) **PostgreSQL 기준 실제 ERD 수준**으로 관계를 더 “그림처럼” 정리 (텍스트로도 가능)
2) 이 스펙대로 **Spring Boot DTO/Controller 경로/엔티티 스켈레톤**을 한 번에 뽑기

원하면 난 2번으로 바로 넘어가서, `/src/main/java/...` 파일 경로까지 딱 맞춰서 생성 지침을 줄게.
::contentReference[oaicite:0]{index=0}
```
