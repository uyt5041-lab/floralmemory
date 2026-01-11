package com.bit.floralmemory.dto.forecast;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ForecastRunRequest {
    private Scope scope;
    private LocalDate trainStart;
    private LocalDate trainEnd;
    private LocalDate forecastStart;
    private LocalDate forecastEnd;
    private List<String> targets;
    private Models models;
    private Ensemble ensemble;

    public ForecastRunRequest() {
    }

    public ForecastRunRequest(Scope scope, LocalDate trainStart, LocalDate trainEnd, LocalDate forecastStart,
            LocalDate forecastEnd, List<String> targets, Models models, Ensemble ensemble) {
        this.scope = scope;
        this.trainStart = trainStart;
        this.trainEnd = trainEnd;
        this.forecastStart = forecastStart;
        this.forecastEnd = forecastEnd;
        this.targets = targets;
        this.models = models;
        this.ensemble = ensemble;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public LocalDate getTrainStart() {
        return trainStart;
    }

    public void setTrainStart(LocalDate trainStart) {
        this.trainStart = trainStart;
    }

    public LocalDate getTrainEnd() {
        return trainEnd;
    }

    public void setTrainEnd(LocalDate trainEnd) {
        this.trainEnd = trainEnd;
    }

    public LocalDate getForecastStart() {
        return forecastStart;
    }

    public void setForecastStart(LocalDate forecastStart) {
        this.forecastStart = forecastStart;
    }

    public LocalDate getForecastEnd() {
        return forecastEnd;
    }

    public void setForecastEnd(LocalDate forecastEnd) {
        this.forecastEnd = forecastEnd;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }

    public Models getModels() {
        return models;
    }

    public void setModels(Models models) {
        this.models = models;
    }

    public Ensemble getEnsemble() {
        return ensemble;
    }

    public void setEnsemble(Ensemble ensemble) {
        this.ensemble = ensemble;
    }

    public static class Scope {
        private String importer;
        private String exporter;
        private String productSlug;
        private List<String> hsCodes;
        private String granularity;

        public Scope() {
        }

        public Scope(String importer, String exporter, String productSlug, List<String> hsCodes, String granularity) {
            this.importer = importer;
            this.exporter = exporter;
            this.productSlug = productSlug;
            this.hsCodes = hsCodes;
            this.granularity = granularity;
        }

        public String getImporter() {
            return importer;
        }

        public void setImporter(String importer) {
            this.importer = importer;
        }

        public String getExporter() {
            return exporter;
        }

        public void setExporter(String exporter) {
            this.exporter = exporter;
        }

        public String getProductSlug() {
            return productSlug;
        }

        public void setProductSlug(String productSlug) {
            this.productSlug = productSlug;
        }

        public List<String> getHsCodes() {
            return hsCodes;
        }

        public void setHsCodes(List<String> hsCodes) {
            this.hsCodes = hsCodes;
        }

        public String getGranularity() {
            return granularity;
        }

        public void setGranularity(String granularity) {
            this.granularity = granularity;
        }
    }

    public static class Models {
        private List<String> shortModels;
        private List<String> midModels;
        private List<String> longModels;

        public Models() {
        }

        public Models(List<String> shortModels, List<String> midModels, List<String> longModels) {
            this.shortModels = shortModels;
            this.midModels = midModels;
            this.longModels = longModels;
        }

        public List<String> getShortModels() {
            return shortModels;
        }

        public void setShortModels(List<String> shortModels) {
            this.shortModels = shortModels;
        }

        public List<String> getMidModels() {
            return midModels;
        }

        public void setMidModels(List<String> midModels) {
            this.midModels = midModels;
        }

        public List<String> getLongModels() {
            return longModels;
        }

        public void setLongModels(List<String> longModels) {
            this.longModels = longModels;
        }
    }

    public static class Ensemble {
        private boolean enabled;
        private Map<String, Map<String, Double>> weights;

        public Ensemble() {
        }

        public Ensemble(boolean enabled, Map<String, Map<String, Double>> weights) {
            this.enabled = enabled;
            this.weights = weights;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Map<String, Map<String, Double>> getWeights() {
            return weights;
        }

        public void setWeights(Map<String, Map<String, Double>> weights) {
            this.weights = weights;
        }
    }
}
