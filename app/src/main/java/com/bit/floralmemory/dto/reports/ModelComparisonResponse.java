package com.bit.floralmemory.dto.reports;

import java.util.List;

@SuppressWarnings("unused")
public class ModelComparisonResponse {
    private Long runId;
    private List<Row> rows;

    public ModelComparisonResponse() {
    }

    public ModelComparisonResponse(Long runId, List<Row> rows) {
        this.runId = runId;
        this.rows = rows;
    }

    public static ModelComparisonResponseBuilder builder() {
        return new ModelComparisonResponseBuilder();
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public static class ModelComparisonResponseBuilder {
        private Long runId;
        private List<Row> rows;

        ModelComparisonResponseBuilder() {
        }

        public ModelComparisonResponseBuilder runId(Long runId) {
            this.runId = runId;
            return this;
        }

        public ModelComparisonResponseBuilder rows(List<Row> rows) {
            this.rows = rows;
            return this;
        }

        public ModelComparisonResponse build() {
            return new ModelComparisonResponse(runId, rows);
        }
    }

    public static class Row {
        private String modelName;
        private Double wape;
        private Double expectedTotalLoss;

        public Row() {
        }

        public Row(String modelName, Double wape, Double expectedTotalLoss) {
            this.modelName = modelName;
            this.wape = wape;
            this.expectedTotalLoss = expectedTotalLoss;
        }

        public static RowBuilder builder() {
            return new RowBuilder();
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public Double getWape() {
            return wape;
        }

        public void setWape(Double wape) {
            this.wape = wape;
        }

        public Double getExpectedTotalLoss() {
            return expectedTotalLoss;
        }

        public void setExpectedTotalLoss(Double expectedTotalLoss) {
            this.expectedTotalLoss = expectedTotalLoss;
        }

        public static class RowBuilder {
            private String modelName;
            private Double wape;
            private Double expectedTotalLoss;

            RowBuilder() {
            }

            public RowBuilder modelName(String modelName) {
                this.modelName = modelName;
                return this;
            }

            public RowBuilder wape(Double wape) {
                this.wape = wape;
                return this;
            }

            public RowBuilder expectedTotalLoss(Double expectedTotalLoss) {
                this.expectedTotalLoss = expectedTotalLoss;
                return this;
            }

            public Row build() {
                return new Row(modelName, wape, expectedTotalLoss);
            }
        }
    }
}
