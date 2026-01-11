# AGENTS.md
# Waste-Aware Demand Forecasting & Ordering System


## 0. Project Definition
This project builds a **waste-aware ordering decision system** for perishable goods.
Forecasting is a required input, but **forecast accuracy is NOT the optimization target**.
The optimization target is **Expected Total Loss** (waste cost + stockout loss).

Short-term, Mid-term, and Long-term time series models are **all implemented and used together**.

Final output is **order quantities**, not forecasts.

---

## 1. Core Principles (Non-Negotiable)
0.read uploaded documents before process a task.
1. Forecasting is mandatory, but not the goal.
2. Ordering decisions must explicitly account for uncertainty.
3. All forecasts are treated as uncertain signals.
4. Decisions are evaluated by cost, not by accuracy alone.
5. Multiple time horizons (Short / Mid / Long) must coexist.
6. Every decision must be explainable in text.
7. Policy parameters (Co/Cu) are optimized via backtest/sweep, not guessed.

---

## 2. Final Outputs (Required)
Each run must generate:

### 2.1 Forecast Outputs
Path: `outputs/forecasts/`

Files:
- forecast_short.csv
- forecast_mid.csv
- forecast_long.csv
- forecast_ensemble.csv

Required columns:
- forecast_date
- sku_id
- channel
- hub
- yhat            (expected demand)
- sigma           (uncertainty)
- model_name
- horizon_days
- train_start
- train_end

---

### 2.2 Ordering Outputs (Primary Artifact)
Path: `outputs/orders/`

File:
- order_recommendation.csv

Required columns:
- order_date
- sku_id
- channel
- hub
- yhat
- sigma
- service_level
- z_value
- on_hand
- lead_time_days
- order_qty
- expected_waste_cost
- expected_stockout_loss
- expected_total_loss
- explanation

---

### 2.3 Reports
Path: `outputs/reports/`

Files:
- model_comparison.csv
- kpi_daily.csv
- top_waste_skus.csv

---

## 3. Data Contract

### 3.1 Sales Data (Required)
Path: `data/raw/sales_daily.csv`

Columns:
- sale_date (YYYY-MM-DD)
- sku_id
- channel
- hub
- units_sold
- unit_price (optional)
- promo_flag (optional)

Rules:
- Do NOT modify raw data.
- Missing dates must be filled with zero in processed data.

---

### 3.2 Inventory Data (Optional but Recommended)
Path: `data/raw/inventory_daily.csv`

Columns:
- inv_date
- sku_id
- hub
- on_hand
- expired_waste (optional)

---

### 3.3 Event Calendar (Strongly Recommended)
Path: `data/calendar/events.csv`

Columns:
- event_date
- event_code
- intensity (1–3)

Events represent known seasonal knowledge and must be injected explicitly.

---

## 4. Folder Structure (Must Be Preserved)
project/
  README.md
  AGENTS.md
  requirements.txt
  data/
    raw/
    processed/
    calendar/
  docs/
    decision_philosophy.md
    kpi_definition.md
  src/
    config/
    io/
    preprocessing/
    features/
    models/
      short_term/
      mid_term/
      long_term/
    ensemble/
    evaluation/
    ordering/
    pipeline/
    utils/
  outputs/
    forecasts/
    orders/
    reports/

---

## 5. Forecasting Models (All Required)

All models MUST implement the same interface:

Methods:
- fit(train_df)
- predict(future_df)

Input:
- train_df: ds (date), y (units), optional regressors
- future_df: ds (date), optional regressors

Output:
- ds
- yhat
- sigma

---

### 5.1 Short-Term Models (2–8 weeks)
Purpose: Immediate ordering decisions (1–7 days)

Required models:
1. ShortBaselineMA
   - Moving Average + Day-of-Week adjustment
2. ShortHoltWinters
   - Holt-Winters Exponential Smoothing (weekly seasonality)

Rules:
- Strongly weight recent data.
- Do NOT model yearly seasonality.
- Sigma must be based on recent residual volatility.

---

### 5.2 Mid-Term Models (3–6 months)
Purpose: Seasonal stability and event sensitivity

Required models:
1. MidHoltWinters
2. MidProphetEvents

Prophet requirements:
- Weekly seasonality ON
- Yearly seasonality OFF by default
- events.csv must be injected as holidays or regressors
- Event windows must be explicitly defined

Sigma:
- Derived from prediction intervals or residuals

---

### 5.3 Long-Term Models (1–3 years)
Purpose: Structural trends and long-run stability

Required models:
1. LongSARIMA (mandatory)
2. LongProphetYearly (optional, if data ≥ 2 years)

SARIMA rules:
- Weekly seasonality required (period = 7)
- Yearly seasonality allowed only if data ≥ 2 years
- SARIMAX with external regressors allowed

Sigma:
- Use forecast variance or residual-based estimation

---

## 6. Ensemble Forecasting (Mandatory)

All horizons must use **Short + Mid + Long forecasts together**.

### 6.1 Default Weights
- Horizon 1–7 days:  Short 0.6 / Mid 0.3 / Long 0.1
- Horizon 8–30 days: Short 0.2 / Mid 0.5 / Long 0.3
- Horizon 31–90 days: Short 0.1 / Mid 0.3 / Long 0.6

### 6.2 Dynamic Weight Adjustment
- Based on recent 4-week performance
- Metrics:
  - WAPE
  - Expected Total Loss
- Poor-performing models must receive reduced weights

Output:
- forecast_ensemble.csv must include model_weights (JSON)

---

## 7. Ordering Decision Engine (Core Logic)

Ordering formula:
order = mean + z * sigma - on_hand_adjusted

Where:
- mean = ensemble yhat
- sigma = ensemble uncertainty
- z = inverse_normal_cdf(service_level)

---

### 7.1 Cost Definitions
- Co: unit waste cost
- Cu: unit stockout loss

Defaults (if unknown):
- Cu ≈ gross_margin + penalty

> Determination Strategy:
> - Start with a reasonable range (e.g., Co=[0.3, 0.5, 0.7], Cu=[1, 2, 3])
> - Run **Policy Sweep** to find the combination that minimizes Expected Total Loss on historical data.

Detailed Breakdown:
- **Co (Overstock Cost)**: (Purchase Price + Disposal/Handling + Storage + Rework) / Unit
- **Cu (Understock Cost)**: (Lost Margin + Expedited Shipping Premium + Reputation Penalty) / Unit

---

### 7.2 Service Level
Service Level:
SL = Cu / (Cu + Co)

SKU-level constraints:
- Grade A: SL ≥ 0.90
- Grade B: 0.75 ≤ SL ≤ 0.90
- Grade C: 0.60 ≤ SL ≤ 0.80

---

### 7.3 Safety Rules
- Upper bound: order_qty ≤ recent P90 sales
- Lower bound (optional): minimum order for Grade A SKUs
- All clipping must be logged

---

### 7.4 Explanation Field (Mandatory)
Each order must include a human-readable explanation:

Example:
"7-day horizon ensemble (S/M/L=0.6/0.3/0.1), SL=0.88 from Cu/Co, capped at P90"

301:
302: ---
303:
304: ### 7.5 Policy Optimization (Sweep & Backtest)
305:
306: We do not rely solely on "gut feeling" for Co/Cu. We use **Policy Sweep**.
307:
308: **Sweep Variables**:
309: - `co_unit`: Range of waste costs
310: - `cu_unit`: Range of stockout penalties
311: - `sigma_inflation` (Default 1.0): Multiplier for uncertainty (conservatism knob)
312: - `yhat_shrink` (Default 0.0): Percentage to reduce forecast (over-ordering prevention)
313:
314: **Objective**:
315: - Minimize **Expected Total Loss** over the backtest period.
316:
317: **Output**:
318: - The "Best Policy" config is saved and used for the actual production order.
319:
320: ---

## 8. KPI Evaluation

### 8.1 Accuracy (Secondary)
- WAPE (preferred)
- MAPE (diagnostic only)

---

### 8.2 Cost-Based KPIs (Primary)
- Expected Waste Cost
- Expected Stockout Loss
- Expected Total Loss

Models are compared by **cost impact**, not accuracy alone.

---

## 9. Execution Pipeline
Entry point: `src/pipeline/run_daily.py`

Steps:
1. Load raw data
2. Build continuous time series
3. Train or load Short/Mid/Long models
4. Generate forecasts
5. Ensemble forecasts
6. Run ordering engine
7. Save outputs
8. Generate reports

---

## 10. Development Order (Strict)
1. Data preprocessing
2. Short-term baseline + ordering engine
3. Mid-term Prophet with events
4. Long-term SARIMA
5. Ensemble logic
6. KPI reporting

---

## 11. Non-Functional Requirements
- Reproducibility preferred
- Clear logging for every decision
- Model failure must degrade gracefully, not crash

---

## 12. Guiding Statement (Do Not Remove)
Forecasting models provide uncertain signals.


추후 대체품목, 대체 공급처를 찾아내는 로직도 추가하도록 한다. 
This system exists to make **better decisions under uncertainty**, not to chase forecast accuracy.