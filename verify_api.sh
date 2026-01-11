#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080"
ECHO_COLOR='\033[0;32m'
NC='\033[0m' # No Color

echo -e "${ECHO_COLOR}=== 1. Starting Forecast Run ===${NC}"
RUN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/forecast/run" \
  -H "Content-Type: application/json" \
  -d '{
    "scope": {
      "importer": "KOR",
      "exporter": "COL",
      "productSlug": "carnation",
      "granularity": "MONTH"
    },
    "trainStart": "2016-01-01",
    "trainEnd": "2024-12-01",
    "forecastStart": "2025-01-01",
    "forecastEnd": "2025-12-01"
  }')

echo "Response: $RUN_RESPONSE"
# Extract runId from ApiResponse: { "code":200, "message": "OK", "data": { "runId": 123 } }
# Use python/jq/grep. Assuming grep simplicity.
RUN_ID=$(echo $RUN_RESPONSE | grep -o '"runId":[0-9]*' | grep -o '[0-9]*')
echo "RunID: $RUN_ID"

if [ -z "$RUN_ID" ]; then
    echo "Failed to get Run ID"
    exit 1
fi

echo -e "\n${ECHO_COLOR}=== 2. Starting Ordering Run (Z-Score) ===${NC}"
ORDER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/ordering/run" \
  -H "Content-Type: application/json" \
  -d "{
    \"runId\": \"$RUN_ID\",
    \"policy\": {
        \"serviceLevel\": { \"target\": 0.95 },
        \"inventory\": { \"onHand\": 0 }
    }
  }")

echo "Response: $ORDER_RESPONSE"

echo -e "\n${ECHO_COLOR}=== 3. Starting Policy Sweep (Optimization) ===${NC}"
SWEEP_RESPONSE=$(curl -s -X POST "$BASE_URL/api/ordering/policy-sweep" \
  -H "Content-Type: application/json" \
  -d "{
    \"runId\": \"$RUN_ID\",
    \"grid\": {
        \"coUnit\": [0.5, 1.0],
        \"cuUnit\": [1.5, 3.0],
        \"sigmaInflation\": [1.0, 1.1],
        \"yhatShrink\": [0.0]
    }
  }")

echo "Response: $SWEEP_RESPONSE"
echo -e "\n${ECHO_COLOR}=== Verification Complete ===${NC}"
