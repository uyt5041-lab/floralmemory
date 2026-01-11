package com.bit.floralmemory.config;

import com.bit.floralmemory.entity.*;
import com.bit.floralmemory.repository.*;
import com.bit.floralmemory.util.MonthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Random;

/**
 * Seeds a minimal pilot dataset so you can hit /api/forecast/run immediately.
 * Toggle in application.yml: app.seed.enabled=true
 */
@Configuration
@RequiredArgsConstructor
public class SeedDataConfig {

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    private final DimCountryRepository countryRepo;
    private final DimProductRepository productRepo;
    private final DimHsCodeRepository hsRepo;
    private final ImportTradeMonthlyFactRepository factRepo;

    @Bean
    CommandLineRunner seedPilotData() {
        return args -> {
            if (!seedEnabled) return;
            if (factRepo.count() > 0) return;

            // Countries
            countryRepo.save(DimCountry.builder().countryCode("KOR").countryName("Korea").build());
            countryRepo.save(DimCountry.builder().countryCode("COL").countryName("Colombia").build());

            // Product
            DimProduct carnation = productRepo.save(DimProduct.builder()
                    .productName("Carnation")
                    .productSlug("carnation")
                    .build());

            // HS code (pilot placeholder)
            DimHsCode hs = hsRepo.save(DimHsCode.builder()
                    .hsCode("060310")
                    .hsDesc("Fresh cut flowers (pilot placeholder)")
                    .parent(null)
                    .build());

            DimCountry kor = countryRepo.findById("KOR").orElseThrow();
            DimCountry col = countryRepo.findById("COL").orElseThrow();

            // Monthly series 2022-01 .. 2024-12
            LocalDate start = LocalDate.of(2022, 1, 1);
            LocalDate end = LocalDate.of(2024, 12, 1);
            Random rnd = new Random(42);

            int t = 0;
            for (LocalDate m = start; !m.isAfter(end); m = m.plusMonths(1)) {
                // synthetic but stable: base + seasonal + trend + noise
                double base = 1000.0;
                double seasonal = 220.0 * Math.sin(2.0 * Math.PI * (m.getMonthValue() - 1) / 12.0);
                double trend = 8.0 * t;
                double noise = rnd.nextGaussian() * 70.0;
                double qty = Math.max(0.0, base + seasonal + trend + noise);
                double value = qty * 2.2; // placeholder unit price

                factRepo.save(ImportTradeMonthlyFact.builder()
                        .month(MonthUtils.normalizeToMonthStart(m))
                        .importer(kor)
                        .exporter(col)
                        .hsCode(hs)
                        .product(carnation)
                        .quantityKg(qty)
                        .valueUsd(value)
                        .sourceName("SEED")
                        .qualityFlags("{}")
                        .createdAt(OffsetDateTime.now())
                        .build());
                t++;
            }
        };
    }
}
