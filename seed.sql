
-- Reference Data
INSERT INTO dim_country (country_code, country_name) VALUES ('KOR', 'Korea') ON CONFLICT DO NOTHING;
INSERT INTO dim_country (country_code, country_name) VALUES ('COL', 'Colombia') ON CONFLICT DO NOTHING;
INSERT INTO dim_product (product_name, product_slug) VALUES ('Carnation', 'carnation') ON CONFLICT DO NOTHING;
INSERT INTO dim_hs_code (hs_code, hs_desc) VALUES ('060310', 'Carnations') ON CONFLICT DO NOTHING;

-- Facts (2022-2024 data)
INSERT INTO import_trade_monthly_fact (month, importer_code, exporter_code, product_id, hs_code, quantity_kg, value_usd, source_name, quality_flags, created_at)
SELECT 
    d::date, 
    'KOR', 
    'COL', 
    (SELECT product_id FROM dim_product WHERE product_slug='carnation'),
    '060310', 
    1000 + (EXTRACT(EPOCH FROM d)::int % 100), 
    5000, 
    'SEED',
    '{}'::jsonb,
    NOW()
FROM generate_series('2022-01-01'::date, '2024-12-01'::date, '1 month'::interval) d
ON CONFLICT DO NOTHING;
