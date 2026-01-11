package com.bit.floralmemory;

import com.bit.floralmemory.dto.forecast.ForecastRunRequest;
import com.bit.floralmemory.service.ForecastService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
public class ManualForecastRunTest {

    @Autowired
    private ForecastService forecastService;

    @Test
    public void runManualForecast() {
        ForecastRunRequest req = new ForecastRunRequest();

        // Scope
        ForecastRunRequest.Scope scope = new ForecastRunRequest.Scope();
        scope.setImporter("KOR");
        scope.setExporter("COL");
        scope.setProductSlug("carnation");
        scope.setHsCodes(List.of("060310"));
        scope.setGranularity("MONTH");
        req.setScope(scope);

        // Dates
        req.setTrainStart(LocalDate.of(2016, 1, 1));
        req.setTrainEnd(LocalDate.of(2024, 12, 1));
        req.setForecastStart(LocalDate.of(2025, 1, 1));
        req.setForecastEnd(LocalDate.of(2025, 12, 1));

        // Specifying targets (optional as per code, but good practice)
        req.setTargets(List.of("quantityKg"));

        // Models (using defaults by not setting or setting explicitly)
        // Leaving null to use defaults from ForecastServiceImpl:
        // Short: ShortHoltWinters, Mid: MidProphetEvents, Long: LongSARIMA

        // Ensemble
        ForecastRunRequest.Ensemble ensemble = new ForecastRunRequest.Ensemble();
        ensemble.setEnabled(true);
        req.setEnsemble(ensemble);

        System.out.println("Starting Forecast Run...");
        Long runId = forecastService.runForecast(req);
        System.out.println("Forecast Run Completed. Run ID: " + runId);
    }
}
