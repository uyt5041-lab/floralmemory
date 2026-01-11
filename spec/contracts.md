SKU 등급 적용:
- A: SL ≥ 0.90
- B: 0.75 ≤ SL < 0.90
- C: 0.60 ≤ SL < 0.75

---

### 6.4 Safety Rules
- 주문 상한선: 최근 24개월 P90 수입량
- 모든 보정은 로그로 남긴다.

---

## 7. KPI Specification

### 7.1 Primary KPI
- **Expected Total Loss**

### 7.2 Secondary KPI
- Expected Waste Cost
- Expected Stockout Loss

### 7.3 Diagnostic KPI
- WAPE
- Model contribution ratio

---

## 8. Ontology Specification (MVP)

### 8.1 Purpose
- HS 코드 ↔ 품목 매핑
- 국가/원산지 개념 통합
- 이벤트/시즌 지식 주입

---

### 8.2 Core Classes
- Product
- HSCode
- Country
- TradeFlow
- Event
- TimePeriod

---

### 8.3 Required Relations
- Product hasHSCode HSCode
- TradeFlow hasProduct Product
- TradeFlow hasOrigin Country
- Product affectedBy Event

---

### 8.4 Required Queries
- HS 코드로 카네이션 매핑 조회
- 연도별 이벤트 캘린더 생성
- HS 계층 구조 조회

---

## 9. API Specification (Backend)

### 9.1 Core Endpoints
- POST /ingestion/run
- GET /data/imports
- POST /forecast/run
- GET /forecast/results
- POST /ordering/run
- GET /ordering/results
- GET /reports/models
- GET /ontology/mappings

---

## 10. UI Specification (MVP)

1. Data & Ingestion Status
2. Forecast Comparison (Short/Mid/Long/Ensemble)
3. Ordering & KPI Dashboard
4. Ontology Mapping View

---

## 11. Non-Functional Requirements

- Reproducibility: 학습 기간/모델 버전 기록
- Observability: 모든 결정 로그화
- Fault tolerance: 단일 모델 실패 허용
- Deployment: Docker Compose 기반

---

## 12. Milestones

- M0: 데이터 수집 + 정규화
- M1: Short 모델 + Ordering Engine
- M2: Prophet(Event) + SARIMA
- M3: Ensemble + KPI + Ontology 연동

---

## 13. Guiding Statement
Forecasting models are uncertain by nature.
This system exists to make **better decisions under uncertainty**, not to chase prediction accuracy.
