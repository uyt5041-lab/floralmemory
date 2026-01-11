# FloralMemory (formerly WasteAware)

This repository contains the backend implementation for the FloralMemory inventory forecasting and ordering system.

## Key Features

- **Forecasting**: Generates demand forecasts using Baseline, Holt-Winters, and Ensemble models.
- **Ordering**: Calculates optimal order quantities using Z-Score service levels and safety stock rules.
- **Policy Sweep (Optimization)**: Grid search engine to find optimal stocking parameters (Co, Cu, Loss metrics).
- **Reports**: Context-aware reporting and analytics.

## Structure

- `app/src/main/java/com/bit/floralmemory`: Main source code.
- `seed.sql`: Initial seed data for verification.
- `verify_api.sh`: Functional verification script.

## Verification

Run `./verify_api.sh` to test the full Forecast -> Optimization -> Ordering flow.
