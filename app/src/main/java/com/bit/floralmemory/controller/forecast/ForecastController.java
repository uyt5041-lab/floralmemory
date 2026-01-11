package com.bit.floralmemory.controller.forecast;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.forecast.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.bit.floralmemory.repository.ForecastEnsembleRepository;
import com.bit.floralmemory.repository.ForecastResultRepository;
import com.bit.floralmemory.service.ForecastService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;
    private final ForecastEnsembleRepository forecastEnsembleRepository;
    private final ForecastResultRepository forecastResultRepository;
    private final ObjectMapper objectMapper;

    @PostMapping("/run")
    public ApiResponse<ForecastRunResponse> run(@RequestBody ForecastRunRequest req) {
        Long runId = forecastService.runForecast(req);
        return ApiResponse.ok(ForecastRunResponse.builder().runId(runId).build());
    }

    @GetMapping("/results")
    public ApiResponse<ForecastResultsResponse> results(@RequestParam Long runId, @RequestParam String type) {
        String t = type == null ? "ENSEMBLE" : type.trim().toUpperCase();

        if ("ENSEMBLE".equals(t)) {
            var series = forecastEnsembleRepository.findByRun_RunIdOrderByMonthAsc(runId).stream()
                    .map(e -> ForecastResultsResponse.Row.builder()
                            .month(e.getMonth())
                            .yhat(e.getYhat())
                            .sigma(e.getSigma())
                            .weights(parseWeights(e.getWeightsJson()))
                            .build())
                    .toList();
            return ApiResponse.ok(ForecastResultsResponse.builder()
                    .runId(runId)
                    .type("ENSEMBLE")
                    .series(series)
                    .build());
        }

        // SHORT | MID | LONG
        List<ForecastResultsResponse.Row> series = forecastResultRepository
                .findByRun_RunIdAndModelFamilyOrderByMonthAsc(runId, t)
                .stream()
                .map(r -> ForecastResultsResponse.Row.builder()
                        .month(r.getMonth())
                        .yhat(r.getYhat())
                        .sigma(r.getSigma())
                        .weights(null)
                        .build())
                .toList();

        return ApiResponse.ok(ForecastResultsResponse.builder()
                .runId(runId)
                .type(t)
                .series(series)
                .build());
    }

    private java.util.Map<String, Double> parseWeights(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<java.util.Map<String, Double>>() {});
        } catch (Exception ex) {
            return null;
        }
    }
}
