-- 0. Cleanup (Optional, for clean state)
DROP TABLE IF EXISTS order_recommendation CASCADE;
DROP TABLE IF EXISTS ordering_run CASCADE;
DROP TABLE IF EXISTS cost_assumption CASCADE;
DROP TABLE IF EXISTS forecast_ensemble CASCADE;
DROP TABLE IF EXISTS forecast_result CASCADE;
DROP TABLE IF EXISTS model_run CASCADE;
DROP TABLE IF EXISTS ontology_mapping_hs_product CASCADE;
DROP TABLE IF EXISTS calendar_event_monthly CASCADE;
DROP TABLE IF EXISTS calendar_event CASCADE;
DROP TABLE IF EXISTS import_trade_monthly_fact CASCADE;
DROP TABLE IF EXISTS import_trade_monthly_raw CASCADE;
DROP TABLE IF EXISTS ingestion_job CASCADE;
DROP TABLE IF EXISTS dim_hs_code CASCADE;
DROP TABLE IF EXISTS dim_product CASCADE;
DROP TABLE IF EXISTS dim_country CASCADE;
DROP TABLE IF EXISTS model_metric CASCADE;

-- 1. Tables

CREATE TABLE dim_country (
  country_code VARCHAR(3) PRIMARY KEY,
  country_name TEXT NOT NULL
);

CREATE TABLE dim_product (
  product_id BIGSERIAL PRIMARY KEY,
  product_name TEXT NOT NULL,
  product_slug TEXT UNIQUE NOT NULL
);

CREATE TABLE dim_hs_code (
  hs_code VARCHAR(10) PRIMARY KEY,
  hs_desc TEXT,
  parent_hs_code VARCHAR(10) NULL REFERENCES dim_hs_code(hs_code)
);

CREATE TABLE ingestion_job (
  ingestion_id BIGSERIAL PRIMARY KEY,
  source_name TEXT NOT NULL,
  status TEXT NOT NULL,
  requested_from DATE NOT NULL,
  requested_to DATE NOT NULL,
  params_json JSONB NOT NULL,
  error_message TEXT,
  started_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  finished_at TIMESTAMPTZ
);

CREATE TABLE import_trade_monthly_raw (
  raw_id BIGSERIAL PRIMARY KEY,
  ingestion_id BIGINT NOT NULL REFERENCES ingestion_job(ingestion_id),
  month DATE NOT NULL,
  reporter_code VARCHAR(3) NOT NULL,
  partner_code VARCHAR(3) NOT NULL,
  hs_code_raw TEXT NOT NULL,
  quantity_kg_raw NUMERIC,
  value_usd_raw NUMERIC,
  unit_raw TEXT,
  payload_json JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_import_raw_month ON import_trade_monthly_raw(month);
CREATE INDEX idx_import_raw_ingestion ON import_trade_monthly_raw(ingestion_id);

CREATE TABLE import_trade_monthly_fact (
  fact_id BIGSERIAL PRIMARY KEY,
  month DATE NOT NULL,
  importer_code VARCHAR(3) NOT NULL REFERENCES dim_country(country_code),
  exporter_code VARCHAR(3) NOT NULL REFERENCES dim_country(country_code),
  hs_code VARCHAR(10) NOT NULL REFERENCES dim_hs_code(hs_code),
  product_id BIGINT NULL REFERENCES dim_product(product_id),
  quantity_kg NUMERIC NOT NULL DEFAULT 0,
  value_usd NUMERIC NOT NULL DEFAULT 0,
  source_name TEXT NOT NULL,
  quality_flags JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (month, importer_code, exporter_code, hs_code, source_name)
);
CREATE INDEX idx_import_fact_month ON import_trade_monthly_fact(month);
CREATE INDEX idx_import_fact_product ON import_trade_monthly_fact(product_id);
CREATE INDEX idx_import_fact_hs ON import_trade_monthly_fact(hs_code);

CREATE TABLE calendar_event (
  event_id BIGSERIAL PRIMARY KEY,
  event_date DATE NOT NULL,
  event_code TEXT NOT NULL,
  intensity SMALLINT NOT NULL DEFAULT 1,
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (event_date, event_code)
);
CREATE INDEX idx_event_date ON calendar_event(event_date);

CREATE TABLE calendar_event_monthly (
  month DATE PRIMARY KEY,
  event_score NUMERIC NOT NULL DEFAULT 0,
  event_detail JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE TABLE ontology_mapping_hs_product (
  mapping_id BIGSERIAL PRIMARY KEY,
  hs_code VARCHAR(10) NOT NULL REFERENCES dim_hs_code(hs_code),
  product_id BIGINT NOT NULL REFERENCES dim_product(product_id),
  confidence NUMERIC NOT NULL DEFAULT 1.0,
  source TEXT NOT NULL DEFAULT 'manual',
  valid_from DATE NULL,
  valid_to DATE NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (hs_code, product_id, source)
);

CREATE TABLE model_run (
  run_id BIGSERIAL PRIMARY KEY,
  run_type TEXT NOT NULL,
  scope_json JSONB NOT NULL,
  train_start DATE NOT NULL,
  train_end DATE NOT NULL,
  forecast_start DATE NOT NULL,
  forecast_end DATE NOT NULL,
  granularity TEXT NOT NULL DEFAULT 'MONTH',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE forecast_result (
  forecast_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  model_name TEXT NOT NULL,
  model_family TEXT NOT NULL,
  month DATE NOT NULL,
  yhat NUMERIC NOT NULL,
  sigma NUMERIC NOT NULL,
  extra_json JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (run_id, model_name, month)
);
CREATE INDEX idx_forecast_run ON forecast_result(run_id);
CREATE INDEX idx_forecast_month ON forecast_result(month);

CREATE TABLE forecast_ensemble (
  ensemble_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  month DATE NOT NULL,
  yhat NUMERIC NOT NULL,
  sigma NUMERIC NOT NULL,
  weights_json JSONB NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (run_id, month)
);

CREATE TABLE cost_assumption (
  cost_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  co_unit NUMERIC NOT NULL,
  cu_unit NUMERIC NOT NULL,
  currency TEXT NOT NULL DEFAULT 'USD',
  rationale TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (run_id)
);

CREATE TABLE ordering_run (
  ordering_run_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  service_level_policy JSONB NOT NULL,
  safety_rules JSONB NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (run_id)
);

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

CREATE TABLE model_metric (
  metric_id BIGSERIAL PRIMARY KEY,
  run_id BIGINT NOT NULL REFERENCES model_run(run_id),
  model_name TEXT NOT NULL,
  wape NUMERIC,
  mape NUMERIC,
  expected_total_loss NUMERIC,
  expected_waste_cost NUMERIC,
  expected_stockout_loss NUMERIC,
  notes JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (run_id, model_name)
);

-- 2. Seed Data

INSERT INTO dim_country (country_code, country_name) VALUES
('KOR', 'South Korea'),
('COL', 'Colombia')
ON CONFLICT DO NOTHING;

INSERT INTO dim_product (product_name, product_slug) VALUES
('Carnation', 'carnation')
ON CONFLICT DO NOTHING;

INSERT INTO dim_hs_code (hs_code, hs_desc) VALUES
('060310', 'Cut flowers and flower buds of a kind suitable for bouquets or for ornamental purposes, fresh, Carnations')
ON CONFLICT DO NOTHING;

-- Map mapping
INSERT INTO ontology_mapping_hs_product (hs_code, product_id, source)
SELECT '060310', product_id, 'manual'
FROM dim_product WHERE product_slug = 'carnation'
ON CONFLICT DO NOTHING;

-- 3. User Data Insertion (Import Facts)
-- Deleting potential duplicates for this source/range first?
DELETE FROM import_trade_monthly_fact 
WHERE source_name = 'USER_UPLOAD' 
  AND importer_code = 'KOR' 
  AND exporter_code = 'COL' 
  AND hs_code = '060310';

INSERT INTO import_trade_monthly_fact (month, importer_code, exporter_code, hs_code, product_id, quantity_kg, value_usd, source_name)
SELECT 
  TO_DATE(d.period || '.01', 'YYYY.MM.DD'), 
  'KOR', 
  'COL', 
  '060310', 
  (SELECT product_id FROM dim_product WHERE product_slug = 'carnation'),
  d.weight, 
  d.usd, 
  'USER_UPLOAD'
FROM (VALUES
  ('2022.01', 97089, 1005039),
  ('2022.02', 70960, 715251),
  ('2022.03', 89743, 896119),
  ('2022.04', 238796, 2985204),
  ('2022.05', 110378, 1299099),
  ('2022.06', 53733, 562238),
  ('2022.07', 73037, 770113),
  ('2022.08', 85840, 907271),
  ('2022.09', 103578, 1106166),
  ('2022.10', 106635, 1147695),
  ('2022.11', 115071, 1192174),
  ('2022.12', 132388, 1355556),
  ('2023.01', 116850, 1208080),
  ('2023.02', 87385, 881819),
  ('2023.03', 127544, 1275320),
  ('2023.04', 322201, 3866492),
  ('2023.05', 107632, 1137782),
  ('2023.06', 66109, 657946),
  ('2023.07', 62698, 597522),
  ('2023.08', 102295, 935168),
  ('2023.09', 111900, 1064836),
  ('2023.10', 132292, 1198885),
  ('2023.11', 124179, 1110903),
  ('2023.12', 147079, 1378986),
  ('2024.01', 146900, 1429336),
  ('2024.02', 112864, 1093214),
  ('2024.03', 119033, 1123358),
  ('2024.04', 319947, 3463952),
  ('2024.05', 103593, 1035461),
  ('2024.06', 72515, 690176),
  ('2024.07', 70711, 686661),
  ('2024.08', 120643, 1119872),
  ('2024.09', 114605, 1062501),
  ('2024.10', 156349, 1450643),
  ('2024.11', 135990, 1293676),
  ('2024.12', 163447, 1582936)
) AS d(period, weight, usd);
