package com.bit.floralmemory;

import com.bit.floralmemory.entity.*;
import com.bit.floralmemory.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Rollback after test
public class FullSystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ImportTradeMonthlyFactRepository factRepo;
    @Autowired
    private DimCountryRepository countryRepo;
    @Autowired
    private DimProductRepository productRepo;
    @Autowired
    private DimHsCodeRepository hsCodeRepo;

    @BeforeEach
    public void setup() {
        // Seed Reference Data
        DimCountry kor = countryRepo.findById("KOR")
                .orElseGet(
                        () -> countryRepo.save(DimCountry.builder().countryCode("KOR").countryName("Korea").build()));

        DimCountry col = countryRepo.findById("COL")
                .orElseGet(() -> countryRepo
                        .save(DimCountry.builder().countryCode("COL").countryName("Colombia").build()));

        DimProduct prod = productRepo.findByProductSlug("carnation")
                .orElseGet(() -> productRepo
                        .save(DimProduct.builder().productName("Carnation").productSlug("carnation").build()));

        DimHsCode hs = hsCodeRepo.findById("060310")
                .orElseGet(() -> hsCodeRepo.save(DimHsCode.builder().hsCode("060310").hsDesc("Carnations").build()));

        // Seed 3 years of data (2022-2024)
        if (factRepo.count() == 0) {
            LocalDate start = LocalDate.of(2022, 1, 1);
            for (int i = 0; i < 36; i++) {
                LocalDate date = start.plusMonths(i);
                factRepo.save(ImportTradeMonthlyFact.builder()
                        .month(date)
                        .importer(kor)
                        .exporter(col)
                        .product(prod)
                        .hsCode(hs)
                        .quantityKg(1000.0 + (i * 10)) // Simple trend
                        .valueUsd(5000.0)
                        .sourceName("TEST")
                        .qualityFlags("{}")
                        .createdAt(OffsetDateTime.now())
                        .build());
            }
        }
    }

    @Test
    public void testFullFlow() throws Exception {
        // 1. Forecast Run
        String forecastJson = """
                {
                    "scope": {
                      "importer": "KOR",
                      "exporter": "COL",
                      "productSlug": "carnation",
                      "granularity": "MONTH"
                    },
                    "trainStart": "2022-01-01",
                    "trainEnd": "2024-12-01",
                    "forecastStart": "2025-01-01",
                    "forecastEnd": "2025-12-01",
                    "ensemble": { "enabled": true }
                }
                """;

        MvcResult runResult = mockMvc.perform(post("/models/forecast/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(forecastJson))
                .andExpect(status().isOk())
                .andReturn();

        String runIdStr = runResult.getResponse().getContentAsString();
        Long runId = Long.parseLong(runIdStr);
        System.out.println("Generated RunID: " + runId);

        // 2. Ordering Run
        String orderJson = String.format("""
                {
                    "runId": "%d",
                    "policy": {
                        "serviceLevel": { "target": 0.95 }
                    }
                }
                """, runId);

        mockMvc.perform(post("/ordering/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isOk());

        // 3. Policy Sweep
        String sweepJson = String.format("""
                {
                    "runId": "%d",
                    "grid": {
                        "coUnit": [0.5],
                        "cuUnit": [1.5],
                        "sigmaInflation": [1.0],
                        "yhatShrink": [0.0]
                    }
                }
                """, runId);

        mockMvc.perform(post("/ordering/policy-sweep")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sweepJson))
                .andExpect(status().isOk());
    }
}
