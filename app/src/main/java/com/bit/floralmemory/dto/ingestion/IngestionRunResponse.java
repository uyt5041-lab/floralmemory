package com.bit.floralmemory.dto.ingestion;

@SuppressWarnings("unused")
public class IngestionRunResponse {
    private Long ingestionId;
    private String status;

    public IngestionRunResponse() {
    }

    public IngestionRunResponse(Long ingestionId, String status) {
        this.ingestionId = ingestionId;
        this.status = status;
    }

    public static IngestionRunResponseBuilder builder() {
        return new IngestionRunResponseBuilder();
    }

    public Long getIngestionId() {
        return ingestionId;
    }

    public void setIngestionId(Long ingestionId) {
        this.ingestionId = ingestionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class IngestionRunResponseBuilder {
        private Long ingestionId;
        private String status;

        IngestionRunResponseBuilder() {
        }

        public IngestionRunResponseBuilder ingestionId(Long ingestionId) {
            this.ingestionId = ingestionId;
            return this;
        }

        public IngestionRunResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public IngestionRunResponse build() {
            return new IngestionRunResponse(ingestionId, status);
        }
    }
}
