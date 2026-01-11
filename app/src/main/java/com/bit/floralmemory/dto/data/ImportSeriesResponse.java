package com.bit.floralmemory.dto.data;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("unused")
public class ImportSeriesResponse {
    private List<Row> series;

    public ImportSeriesResponse() {
    }

    public ImportSeriesResponse(List<Row> series) {
        this.series = series;
    }

    public static ImportSeriesResponseBuilder builder() {
        return new ImportSeriesResponseBuilder();
    }

    public List<Row> getSeries() {
        return series;
    }

    public void setSeries(List<Row> series) {
        this.series = series;
    }

    public static class ImportSeriesResponseBuilder {
        private List<Row> series;

        ImportSeriesResponseBuilder() {
        }

        public ImportSeriesResponseBuilder series(List<Row> series) {
            this.series = series;
            return this;
        }

        public ImportSeriesResponse build() {
            return new ImportSeriesResponse(series);
        }
    }

    public static class Row {
        private LocalDate month;
        private Double quantityKg;
        private Double valueUsd;
        private String hsCode;

        public Row() {
        }

        public Row(LocalDate month, Double quantityKg, Double valueUsd, String hsCode) {
            this.month = month;
            this.quantityKg = quantityKg;
            this.valueUsd = valueUsd;
            this.hsCode = hsCode;
        }

        public static RowBuilder builder() {
            return new RowBuilder();
        }

        public LocalDate getMonth() {
            return month;
        }

        public void setMonth(LocalDate month) {
            this.month = month;
        }

        public Double getQuantityKg() {
            return quantityKg;
        }

        public void setQuantityKg(Double quantityKg) {
            this.quantityKg = quantityKg;
        }

        public Double getValueUsd() {
            return valueUsd;
        }

        public void setValueUsd(Double valueUsd) {
            this.valueUsd = valueUsd;
        }

        public String getHsCode() {
            return hsCode;
        }

        public void setHsCode(String hsCode) {
            this.hsCode = hsCode;
        }

        public static class RowBuilder {
            private LocalDate month;
            private Double quantityKg;
            private Double valueUsd;
            private String hsCode;

            RowBuilder() {
            }

            public RowBuilder month(LocalDate month) {
                this.month = month;
                return this;
            }

            public RowBuilder quantityKg(Double quantityKg) {
                this.quantityKg = quantityKg;
                return this;
            }

            public RowBuilder valueUsd(Double valueUsd) {
                this.valueUsd = valueUsd;
                return this;
            }

            public RowBuilder hsCode(String hsCode) {
                this.hsCode = hsCode;
                return this;
            }

            public Row build() {
                return new Row(month, quantityKg, valueUsd, hsCode);
            }
        }
    }
}
