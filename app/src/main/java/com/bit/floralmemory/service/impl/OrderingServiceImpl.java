package com.bit.floralmemory.service.impl;

import com.bit.floralmemory.dto.ordering.*;
import com.bit.floralmemory.entity.ModelRun;
import com.bit.floralmemory.entity.OrderingRun;
import com.bit.floralmemory.entity.PolicySweepResult;
import com.bit.floralmemory.entity.PolicySweepRun;
import com.bit.floralmemory.repository.ModelRunRepository;
import com.bit.floralmemory.repository.OrderingRunRepository;
import com.bit.floralmemory.repository.PolicySweepResultRepository;
import com.bit.floralmemory.repository.PolicySweepRunRepository;
import com.bit.floralmemory.service.OrderingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderingServiceImpl implements OrderingService {

        private final ModelRunRepository modelRunRepo;
        private final OrderingRunRepository orderingRunRepo;
        private final PolicySweepRunRepository sweepRunRepo;
        private final PolicySweepResultRepository sweepResultRepo;
        private final com.bit.floralmemory.repository.ForecastEnsembleRepository forecastEnsembleRepo;
        private final com.bit.floralmemory.repository.OrderingResultRepository orderingResultRepo;
        private final ObjectMapper objectMapper;

        public OrderingServiceImpl(ModelRunRepository modelRunRepo, OrderingRunRepository orderingRunRepo,
                        PolicySweepRunRepository sweepRunRepo, PolicySweepResultRepository sweepResultRepo,
                        com.bit.floralmemory.repository.ForecastEnsembleRepository forecastEnsembleRepo,
                        com.bit.floralmemory.repository.OrderingResultRepository orderingResultRepo,
                        ObjectMapper objectMapper) {
                this.modelRunRepo = modelRunRepo;
                this.orderingRunRepo = orderingRunRepo;
                this.sweepRunRepo = sweepRunRepo;
                this.sweepResultRepo = sweepResultRepo;
                this.forecastEnsembleRepo = forecastEnsembleRepo;
                this.orderingResultRepo = orderingResultRepo;
                this.objectMapper = objectMapper;
        }

        @Override
        @Transactional
        public Long runOrdering(OrderingRunRequest req) {
                try {
                        System.out.println("DEBUG: runOrdering called with RunID=" + req.getRunId());
                        // Basic skeleton created before
                        ModelRun run = modelRunRepo.findById(Long.valueOf(req.getRunId()))
                                        .orElseThrow(() -> new IllegalArgumentException("Run not found"));

                        String slJson = "{}";
                        String safetyJson = "{}";
                        try {
                                if (req.getPolicy() != null) {
                                        if (req.getPolicy().getServiceLevel() != null)
                                                slJson = objectMapper
                                                                .writeValueAsString(req.getPolicy().getServiceLevel());
                                        if (req.getPolicy().getSafetyRules() != null)
                                                safetyJson = objectMapper
                                                                .writeValueAsString(req.getPolicy().getSafetyRules());
                                }
                        } catch (Exception e) {
                                System.err.println("DEBUG: JSON serialization error: " + e.getMessage());
                        }

                        System.out.println("DEBUG: Saving OrderingRun...");
                        OrderingRun oRun = OrderingRun.builder()
                                        .modelRun(run)
                                        .slJson(slJson)
                                        .safetyJson(safetyJson)
                                        .createdAt(OffsetDateTime.now())
                                        .build();
                        oRun = orderingRunRepo.save(oRun); // Re-assign to ensure ID if needed, though object is mutated
                                                           // usually
                        System.out.println("DEBUG: OrderingRun Saved. ID=" + oRun.getOrderRunId());

                        List<com.bit.floralmemory.entity.ForecastEnsemble> forecasts = forecastEnsembleRepo
                                        .findByRun_RunIdOrderByMonthAsc(run.getRunId());
                        System.out.println("DEBUG: Found " + forecasts.size() + " forecasts.");

                        if (forecasts.isEmpty()) {
                                // Fallback or warning?
                        }

                        double serviceLevel = 0.5; // default
                        if (req.getPolicy() != null && req.getPolicy().getServiceLevel() != null) {
                                java.util.Map<String, Object> slMap = req.getPolicy().getServiceLevel();
                                Object val = slMap.get("target");
                                if (val == null)
                                        val = slMap.get("value");
                                if (val == null)
                                        val = slMap.get("default");

                                if (val instanceof Number) {
                                        serviceLevel = ((Number) val).doubleValue();
                                }
                        }

                        // Cap Service Level to reasonable bounds
                        serviceLevel = Math.max(0.01, Math.min(0.999, serviceLevel));
                        double z = com.bit.floralmemory.util.StatsUtils.inverseNormalCdf(serviceLevel);

                        List<com.bit.floralmemory.entity.OrderingResult> results = new ArrayList<>();
                        for (com.bit.floralmemory.entity.ForecastEnsemble f : forecasts) {
                                double yhat = f.getYhat();
                                double sigma = f.getSigma();
                                double onHand = 0.0; // TODO: Fetch from inventory service/repo

                                // Key Formula
                                double safeStock = z * sigma;
                                double rawQty = yhat + safeStock - onHand;
                                double orderQty = Math.max(0.0, rawQty);

                                // TODO: Apply Safety Rules (P90 Cap)
                                // double cap = calculateP90(...);
                                // if (orderQty > cap) { explanation += " Capped at " + cap; orderQty = cap; }

                                String explanation = String.format(
                                                "SL=%.2f(Z=%.2f) Forecast=%.2f Sigma=%.2f SafeStock=%.2f OnHand=%.2f",
                                                serviceLevel, z, yhat, sigma, safeStock, onHand);

                                com.bit.floralmemory.entity.OrderingResult res = com.bit.floralmemory.entity.OrderingResult
                                                .builder()
                                                .orderingRun(oRun)
                                                .skuId("N/A") // In pilot, run is for 1 SKU scope.
                                                .date(f.getMonth())
                                                .yhat(yhat)
                                                .sigma(sigma)
                                                .serviceLevel(serviceLevel)
                                                .zValue(z)
                                                .onHand(onHand)
                                                .orderQty(orderQty)
                                                .expectedWasteCost(0.0) // To be calculated
                                                .expectedStockoutLoss(0.0) // To be calculated
                                                .expectedTotalLoss(0.0) // To be calculated
                                                .explanation(explanation)
                                                .createdAt(OffsetDateTime.now())
                                                .build();
                                results.add(res);
                        }
                        orderingResultRepo.saveAll(results);

                        return oRun.getOrderRunId();
                } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                }
        }

        @Override
        @Transactional
        public PolicySweepResponse runPolicySweep(PolicySweepRequest req) {
                // 1. Load run
                ModelRun run = modelRunRepo.findById(Long.valueOf(req.getRunId()))
                                .orElseThrow(() -> new IllegalArgumentException("Run not found: " + req.getRunId()));

                // 2. Persist Sweep Run
                String gridJson = "{}";
                try {
                        gridJson = objectMapper.writeValueAsString(req.getGrid());
                } catch (Exception ignored) {
                }

                PolicySweepRun sweepRun = PolicySweepRun.builder()
                                .run(run)
                                .gridJson(gridJson)
                                .objective("MIN_EXPECTED_TOTAL_LOSS")
                                .createdAt(OffsetDateTime.now())
                                .build();
                sweepRunRepo.save(sweepRun);

                // 3. Execute Search (Grid Search MVP)
                // For each combination of Co/Cu/Sigma/Shrink:
                // Calculate Expected Total Loss over the Backtest range (TrainEnd+1 ~
                // ForecastEnd)
                // Store result

                List<Double> coList = (req.getGrid().getCoUnit() != null && !req.getGrid().getCoUnit().isEmpty())
                                ? req.getGrid().getCoUnit()
                                : List.of(req.getGrid().getDefaultCo() != null ? req.getGrid().getDefaultCo() : 0.5);
                List<Double> cuList = (req.getGrid().getCuUnit() != null && !req.getGrid().getCuUnit().isEmpty())
                                ? req.getGrid().getCuUnit()
                                : List.of(req.getGrid().getDefaultCu() != null ? req.getGrid().getDefaultCu() : 1.5);

                // Defaults for inflation/shrink if not provided
                List<Double> infList = List.of(1.0); // Simple MVP
                List<Double> shrinkList = List.of(0.0);

                // Fetch Forecasts once
                List<com.bit.floralmemory.entity.ForecastEnsemble> forecasts = forecastEnsembleRepo
                                .findByRun_RunIdOrderByMonthAsc(run.getRunId());

                double minLoss = Double.MAX_VALUE;
                PolicySweepResult bestResult = null;

                for (Double co : coList) {
                        for (Double cu : cuList) {
                                for (Double inf : infList) {
                                        for (Double shrink : shrinkList) {
                                                // 1. Calculate Params for this combination
                                                double serviceLevel = cu / (co + cu);
                                                double z = com.bit.floralmemory.util.StatsUtils
                                                                .inverseNormalCdf(serviceLevel);

                                                double totalLoss = 0.0;
                                                double totalWaste = 0.0;
                                                double totalStockout = 0.0;

                                                // 2. Simulate over backtest period (forecasts)
                                                for (com.bit.floralmemory.entity.ForecastEnsemble f : forecasts) {
                                                        double yhatRaw = f.getYhat();
                                                        double sigmaRaw = f.getSigma();

                                                        // Apply Policy Transforms
                                                        double yhat = yhatRaw * (1.0 - shrink);
                                                        double sigma = sigmaRaw * inf;

                                                        // Order Qty
                                                        double qty = yhat + z * sigma;

                                                        // Analytical Expected Loss (Newsvendor Loss Function)
                                                        // L(Q) = Co * E[(Q-D)+] + Cu * E[(D-Q)+]
                                                        // Standard Normal Loss L(z) = pdf(z) - z(1 - cdf(z)) is for
                                                        // stockouts
                                                        // Actually, simpler generic formula for Normal Dist:
                                                        // E[Shortage] = sigma * (pdf(z) - z * (1 - cdf(z)))
                                                        // E[Overage] = sigma * (pdf(z) + z * cdf(z)) <-- Wait,
                                                        // symmetry?
                                                        // Let's rely on standard loss function G(z):
                                                        // G(z) = pdf(z) - z * (1 - Cdf(z))
                                                        // Expected Shortage (Units) = sigma * G(z)
                                                        // Expected Overage (Units) = sigma * G(-z) ?? Or derived?
                                                        // Let's use direct integration logic approximation or just the
                                                        // property:
                                                        // Expected Shortage = sigma * (pdf(z) - z * (1-cdf(z)))
                                                        // Expected Overage = Expected Shortage + (Q - Mean) (Identity:
                                                        // E[Over] - E[Short] = Q - Mean)

                                                        double pdf = com.bit.floralmemory.util.StatsUtils.normalPdf(z);
                                                        double cdf = com.bit.floralmemory.util.StatsUtils.normalCdf(z);

                                                        double g_z = pdf - z * (1.0 - cdf); // Loss function for
                                                                                            // shortage
                                                        double expectedShortageUnits = sigma * g_z;
                                                        double expectedOverageUnits = expectedShortageUnits
                                                                        + (qty - yhat);

                                                        double wCost = expectedOverageUnits * co;
                                                        double sLoss = expectedShortageUnits * cu;

                                                        totalWaste += wCost;
                                                        totalStockout += sLoss;
                                                        totalLoss += (wCost + sLoss);
                                                }

                                                // 3. Build Result
                                                PolicySweepResult res = PolicySweepResult.builder()
                                                                .sweepRun(sweepRun)
                                                                .coUnit(co)
                                                                .cuUnit(cu)
                                                                .serviceLevel(serviceLevel)
                                                                .zValue(z)
                                                                .sigmaInflation(inf)
                                                                .yhatShrink(shrink)
                                                                .expectedTotalLoss(totalLoss)
                                                                .expectedWasteCost(totalWaste)
                                                                .expectedStockoutLoss(totalStockout)
                                                                .constraintsJson("{}")
                                                                .createdAt(OffsetDateTime.now())
                                                                .build();

                                                // 4. Check Best
                                                if (totalLoss < minLoss) {
                                                        minLoss = totalLoss;
                                                        bestResult = res;
                                                }

                                                // Save all? Or just best? Spec says "Best Policy config is saved" but
                                                // usually we want to see the sweep.
                                                // Let's save all for MVP debugging, maybe limit if grid is huge.
                                                sweepResultRepo.save(res);
                                        }
                                }
                        }
                }

                if (bestResult != null) {
                        bestResult.setIsBest(true);
                        sweepResultRepo.save(bestResult);
                } else {
                        // Should not happen if grid not empty
                        throw new IllegalStateException("Policy Sweep failed to generate any results");
                }

                return PolicySweepResponse.builder()
                                .sweepId(sweepRun.getSweepId())
                                .best(PolicySweepResponse.BestPolicy.builder()
                                                .coUnit(bestResult.getCoUnit())
                                                .cuUnit(bestResult.getCuUnit())
                                                .serviceLevel(bestResult.getServiceLevel())
                                                .zValue(bestResult.getZValue())
                                                .sigmaInflation(bestResult.getSigmaInflation())
                                                .yhatShrink(bestResult.getYhatShrink())
                                                .expectedTotalLoss(bestResult.getExpectedTotalLoss())
                                                .build())
                                .build();
        }
}
