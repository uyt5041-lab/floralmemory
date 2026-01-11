package com.bit.floralmemory.service.impl;

import com.bit.floralmemory.dto.forecast.ForecastRunRequest;
import com.bit.floralmemory.entity.*;
import com.bit.floralmemory.repository.*;
import com.bit.floralmemory.service.ForecastService;
import com.bit.floralmemory.util.MonthUtils;
import com.bit.floralmemory.util.StatsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Fast-path implementation:
 * - Reads monthly import series from DB
 * - Generates forecasts with simple deterministic baselines (short/mid/long)
 * - Persists model_run, forecast_result, forecast_ensemble
 *
 * Later you can swap the three baseline models with calls to an external
 * model-service (FastAPI).
 */
@Service

public class ForecastServiceImpl implements ForecastService {

    private final ImportTradeMonthlyFactRepository importFactRepo;
    private final ModelRunRepository modelRunRepo;
    private final ForecastResultRepository forecastResultRepo;
    private final ForecastEnsembleRepository forecastEnsembleRepo;
    private final ObjectMapper objectMapper;

    public ForecastServiceImpl(ImportTradeMonthlyFactRepository importFactRepo, ModelRunRepository modelRunRepo,
            ForecastResultRepository forecastResultRepo, ForecastEnsembleRepository forecastEnsembleRepo,
            ObjectMapper objectMapper) {
        this.importFactRepo = importFactRepo;
        this.modelRunRepo = modelRunRepo;
        this.forecastResultRepo = forecastResultRepo;
        this.forecastEnsembleRepo = forecastEnsembleRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Long runForecast(ForecastRunRequest req) {
        validate(req);

        // 1) Normalize month boundaries
        LocalDate trainStart = MonthUtils.normalizeToMonthStart(req.getTrainStart());
        LocalDate trainEnd = MonthUtils.normalizeToMonthStart(req.getTrainEnd());
        LocalDate fcStart = MonthUtils.normalizeToMonthStart(req.getForecastStart());
        LocalDate fcEnd = MonthUtils.normalizeToMonthStart(req.getForecastEnd());

        // 2) Load training series
        List<ImportTradeMonthlyFact> facts = importFactRepo.findSeries(
                trainStart, trainEnd,
                req.getScope().getImporter(),
                req.getScope().getExporter(),
                req.getScope().getProductSlug());
        if (facts.isEmpty()) {
            throw new IllegalStateException("No training data found in import_trade_monthly_fact for the given scope.");
        }

        // Build ordered series map
        LinkedHashMap<LocalDate, Double> y = new LinkedHashMap<>();
        for (ImportTradeMonthlyFact f : facts) {
            y.put(MonthUtils.normalizeToMonthStart(f.getMonth()),
                    f.getQuantityKg() == null ? 0.0 : f.getQuantityKg());
        }

        // 3) Persist model_run
        String scopeJson;
        try {
            scopeJson = objectMapper.writeValueAsString(req.getScope());
        } catch (Exception e) {
            scopeJson = "{}";
        }

        ModelRun run = ModelRun.builder()
                .runType("PREDICT")
                .scopeJson(scopeJson)
                .trainStart(trainStart)
                .trainEnd(trainEnd)
                .forecastStart(fcStart)
                .forecastEnd(fcEnd)
                .granularity(Optional.ofNullable(req.getScope().getGranularity()).orElse("MONTH"))
                .createdAt(java.time.OffsetDateTime.now())
                .build();
        run = modelRunRepo.save(run);

        // 4) Generate future months
        List<LocalDate> futureMonths = enumerateMonths(fcStart, fcEnd);

        // 5) Run baseline models
        Map<String, Map<LocalDate, Pred>> modelPreds = new LinkedHashMap<>();
        // Always run at least one per family (spec default)
        String shortModel = pickFirst(req.getModels() == null ? null : req.getModels().getShortModels(),
                "ShortHoltWinters");
        String midModel = pickFirst(req.getModels() == null ? null : req.getModels().getMidModels(),
                "MidProphetEvents");
        String longModel = pickFirst(req.getModels() == null ? null : req.getModels().getLongModels(), "LongSARIMA");

        modelPreds.put(shortModel, shortBaseline(y, futureMonths));
        modelPreds.put(midModel, midBaselineSeasonalAvg(y, futureMonths));
        modelPreds.put(longModel, longBaselineTrendPlusSeason(y, futureMonths));

        // 6) Persist forecast_result rows
        persistForecastResults(run, shortModel, "SHORT", modelPreds.get(shortModel));
        persistForecastResults(run, midModel, "MID", modelPreds.get(midModel));
        persistForecastResults(run, longModel, "LONG", modelPreds.get(longModel));

        // 7) Ensemble (spec weights per horizon bucket)
        boolean ensembleEnabled = req.getEnsemble() == null || req.getEnsemble().isEnabled();
        if (ensembleEnabled) {
            for (LocalDate m : futureMonths) {
                long horizonMonths = ChronoUnit.MONTHS.between(fcStart, m) + 1; // 1..N
                FamilyWeights fw = weightsByHorizon(horizonMonths);

                // Map family weights to model weights (1 model each in pilot)
                Map<String, Double> w = new LinkedHashMap<>();
                w.put(shortModel, fw.shortW);
                w.put(midModel, fw.midW);
                w.put(longModel, fw.longW);

                // Combine
                double yhat = 0.0;
                double var = 0.0;
                for (Map.Entry<String, Double> e : w.entrySet()) {
                    Pred p = modelPreds.get(e.getKey()).get(m);
                    double ww = e.getValue();
                    yhat += ww * p.yhat;
                    var += (ww * ww) * (p.sigma * p.sigma);
                }
                double sigma = Math.sqrt(var);

                String weightsJson;
                try {
                    weightsJson = objectMapper.writeValueAsString(w);
                } catch (Exception e) {
                    weightsJson = "{}";
                }

                forecastEnsembleRepo.save(ForecastEnsemble.builder()
                        .run(run)
                        .month(m)
                        .yhat(yhat)
                        .sigma(sigma)
                        .weightsJson(weightsJson)
                        .createdAt(java.time.OffsetDateTime.now())
                        .build());
            }
        }

        return run.getRunId();
    }

    private void validate(ForecastRunRequest req) {
        if (req == null || req.getScope() == null)
            throw new IllegalArgumentException("scope is required");
        if (req.getTrainStart() == null || req.getTrainEnd() == null)
            throw new IllegalArgumentException("trainStart/trainEnd required");
        if (req.getForecastStart() == null || req.getForecastEnd() == null)
            throw new IllegalArgumentException("forecastStart/forecastEnd required");
        if (req.getScope().getImporter() == null || req.getScope().getExporter() == null)
            throw new IllegalArgumentException("importer/exporter required");
        if (req.getScope().getProductSlug() == null)
            throw new IllegalArgumentException("productSlug required");
    }

    private List<LocalDate> enumerateMonths(LocalDate fromMonth, LocalDate toMonth) {
        List<LocalDate> out = new ArrayList<>();
        LocalDate cur = fromMonth;
        while (!cur.isAfter(toMonth)) {
            out.add(cur);
            cur = cur.plusMonths(1);
        }
        return out;
    }

    private String pickFirst(List<String> list, String fallback) {
        if (list == null || list.isEmpty() || list.get(0) == null || list.get(0).isBlank())
            return fallback;
        return list.get(0);
    }

    private void persistForecastResults(ModelRun run, String modelName, String family, Map<LocalDate, Pred> preds) {
        for (Map.Entry<LocalDate, Pred> e : preds.entrySet()) {
            forecastResultRepo.save(ForecastResult.builder()
                    .run(run)
                    .modelName(modelName)
                    .modelFamily(family)
                    .month(e.getKey())
                    .yhat(e.getValue().yhat)
                    .sigma(e.getValue().sigma)
                    .extraJson("{}")
                    .createdAt(java.time.OffsetDateTime.now())
                    .build());
        }
    }

    /** SHORT: simple moving average of last 3 months */
    private Map<LocalDate, Pred> shortBaseline(LinkedHashMap<LocalDate, Double> y, List<LocalDate> futureMonths) {
        List<Map.Entry<LocalDate, Double>> items = new ArrayList<>(y.entrySet());
        int n = items.size();
        int window = Math.min(3, n);
        List<Double> last = new ArrayList<>();
        for (int i = n - window; i < n; i++)
            last.add(items.get(i).getValue());
        double mu = StatsUtils.mean(last);

        // sigma from last 12 months variability (fallback: 0.1*mu)
        int sigmaWindow = Math.min(12, n);
        List<Double> sigmaXs = new ArrayList<>();
        for (int i = n - sigmaWindow; i < n; i++)
            sigmaXs.add(items.get(i).getValue());
        double sigma = Math.max(StatsUtils.stddev(sigmaXs), Math.abs(mu) * 0.10);

        Map<LocalDate, Pred> out = new LinkedHashMap<>();
        for (LocalDate m : futureMonths) {
            out.put(m, new Pred(mu, sigma));
        }
        return out;
    }

    /** MID: month-of-year average (seasonal average) */
    private Map<LocalDate, Pred> midBaselineSeasonalAvg(LinkedHashMap<LocalDate, Double> y,
            List<LocalDate> futureMonths) {
        Map<Integer, List<Double>> byMonth = new HashMap<>();
        for (Map.Entry<LocalDate, Double> e : y.entrySet()) {
            int mo = e.getKey().getMonthValue();
            byMonth.computeIfAbsent(mo, k -> new ArrayList<>()).add(e.getValue());
        }
        Map<LocalDate, Pred> out = new LinkedHashMap<>();
        for (LocalDate m : futureMonths) {
            int mo = m.getMonthValue();
            List<Double> xs = byMonth.getOrDefault(mo, List.of());
            double mu = xs.isEmpty() ? StatsUtils.mean(new ArrayList<>(y.values())) : StatsUtils.mean(xs);
            double sigma = xs.size() >= 2 ? StatsUtils.stddev(xs) : Math.abs(mu) * 0.12;
            out.put(m, new Pred(mu, sigma));
        }
        return out;
    }

    /** LONG: linear trend + additive seasonal factors */
    private Map<LocalDate, Pred> longBaselineTrendPlusSeason(LinkedHashMap<LocalDate, Double> y,
            List<LocalDate> futureMonths) {
        List<Map.Entry<LocalDate, Double>> items = new ArrayList<>(y.entrySet());
        int n = items.size();
        // simple OLS on index t=0..n-1
        double sumT = 0, sumY = 0, sumTT = 0, sumTY = 0;
        for (int t = 0; t < n; t++) {
            double yy = items.get(t).getValue();
            sumT += t;
            sumY += yy;
            sumTT += t * (double) t;
            sumTY += t * yy;
        }
        double denom = n * sumTT - sumT * sumT;
        double b = denom == 0 ? 0 : (n * sumTY - sumT * sumY) / denom;
        double a = (sumY - b * sumT) / n;

        // seasonal factors by month-of-year from residuals
        Map<Integer, List<Double>> residByMonth = new HashMap<>();
        List<Double> residuals = new ArrayList<>();
        for (int t = 0; t < n; t++) {
            double yy = items.get(t).getValue();
            double yTrend = a + b * t;
            double r = yy - yTrend;
            residuals.add(r);
            residByMonth.computeIfAbsent(items.get(t).getKey().getMonthValue(), k -> new ArrayList<>()).add(r);
        }
        Map<Integer, Double> season = new HashMap<>();
        for (Map.Entry<Integer, List<Double>> e : residByMonth.entrySet()) {
            season.put(e.getKey(), StatsUtils.mean(e.getValue()));
        }
        double sigma = Math.max(StatsUtils.stddev(residuals),
                Math.abs(StatsUtils.mean(new ArrayList<>(y.values()))) * 0.15);

        // forecast
        LocalDate lastTrainMonth = items.get(n - 1).getKey();
        long baseIndex = 0; // index for lastTrainMonth
        // compute baseIndex corresponding to lastTrainMonth in the original t scale
        baseIndex = n - 1;

        Map<LocalDate, Pred> out = new LinkedHashMap<>();
        for (LocalDate m : futureMonths) {
            long dt = ChronoUnit.MONTHS.between(lastTrainMonth, m);
            double t = baseIndex + dt;
            double yTrend = a + b * t;
            double s = season.getOrDefault(m.getMonthValue(), 0.0);
            out.put(m, new Pred(yTrend + s, sigma));
        }
        return out;
    }

    private FamilyWeights weightsByHorizon(long horizonMonths) {
        // Spec default (monthly horizon buckets)
        if (horizonMonths <= 3)
            return new FamilyWeights(0.4, 0.4, 0.2);
        if (horizonMonths <= 6)
            return new FamilyWeights(0.2, 0.4, 0.4);
        return new FamilyWeights(0.1, 0.3, 0.6);
    }

    private record Pred(double yhat, double sigma) {
    }

    private record FamilyWeights(double shortW, double midW, double longW) {
    }
}
