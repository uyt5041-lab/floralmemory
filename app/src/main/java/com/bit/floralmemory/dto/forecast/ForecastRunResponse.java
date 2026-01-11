package com.bit.floralmemory.dto.forecast;

@SuppressWarnings("unused")
public class ForecastRunResponse {
    private Long runId;

    public ForecastRunResponse() {
    }

    public ForecastRunResponse(Long runId) {
        this.runId = runId;
    }

    public static ForecastRunResponseBuilder builder() {
        return new ForecastRunResponseBuilder();
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public static class ForecastRunResponseBuilder {
        private Long runId;

        ForecastRunResponseBuilder() {
        }

        public ForecastRunResponseBuilder runId(Long runId) {
            this.runId = runId;
            return this;
        }

        public ForecastRunResponse build() {
            return new ForecastRunResponse(runId);
        }
    }
}
