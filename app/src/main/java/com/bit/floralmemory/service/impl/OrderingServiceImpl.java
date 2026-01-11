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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderingServiceImpl implements OrderingService {

    private final ModelRunRepository modelRunRepo;
    private final OrderingRunRepository orderingRunRepo;
    private final PolicySweepRunRepository sweepRunRepo;
    private final PolicySweepResultRepository sweepResultRepo;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Long runOrdering(OrderingRunRequest req) {
        // Basic skeleton created before
        ModelRun run = modelRunRepo.findById(req.getRunId())
                .orElseThrow(() -> new IllegalArgumentException("Run not found"));

        String slJson = "{}", safetyJson = "{}";
        try {
            slJson = objectMapper.writeValueAsString(req.getPolicy().getServiceLevel());
            safetyJson = objectMapper.writeValueAsString(req.getPolicy().getSafetyRules());
        } catch (Exception ignored) {
        }

        OrderingRun oRun = OrderingRun.builder()
                .run(run)
                .serviceLevelPolicy(slJson)
                .safetyRules(safetyJson)
                .createdAt(OffsetDateTime.now())
                .build();
        orderingRunRepo.save(oRun);

        // TODO: Actual ordering logic (calculating orderQty based on SL and Forecast)
        // would go here

        return oRun.getOrderingRunId();
    }

    @Override
    @Transactional
    public PolicySweepResponse runPolicySweep(PolicySweepRequest req) {
        // 1. Load run
        ModelRun run = modelRunRepo.findById(req.getRunId())
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

        List<Double> coList = req.getGrid().getCoUnit();
        List<Double> cuList = req.getGrid().getCuUnit();

        double minLoss = Double.MAX_VALUE;
        PolicySweepResult bestResult = null;

        // Simplified interaction for MVP: pick first if list not empty, else default
        // In real impl, 4 nested loops.
        // Let's just create one mock result as "Best" to demonstrate flow.

        PolicySweepResult mockBest = PolicySweepResult.builder()
                .sweepRun(sweepRun)
                .coUnit(coList != null && !coList.isEmpty() ? coList.get(0) : 0.5)
                .cuUnit(cuList != null && !cuList.isEmpty() ? cuList.get(0) : 1.5)
                .serviceLevel(0.75) // derived
                .zValue(0.67) // derived
                .sigmaInflation(1.0)
                .yhatShrink(0.0)
                .expectedTotalLoss(1000.0)
                .expectedWasteCost(200.0)
                .expectedStockoutLoss(800.0)
                .constraintsJson("{}")
                .isBest(true)
                .createdAt(OffsetDateTime.now())
                .build();
        sweepResultRepo.save(mockBest);

        return PolicySweepResponse.builder()
                .sweepId(sweepRun.getSweepId())
                .best(PolicySweepResponse.BestPolicy.builder()
                        .coUnit(mockBest.getCoUnit())
                        .cuUnit(mockBest.getCuUnit())
                        .serviceLevel(mockBest.getServiceLevel())
                        .zValue(mockBest.getZValue())
                        .sigmaInflation(mockBest.getSigmaInflation())
                        .yhatShrink(mockBest.getYhatShrink())
                        .expectedTotalLoss(mockBest.getExpectedTotalLoss())
                        .build())
                .build();
    }
}
