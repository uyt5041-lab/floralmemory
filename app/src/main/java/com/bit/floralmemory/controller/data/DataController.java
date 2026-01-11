package com.bit.floralmemory.controller.data;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.data.ImportSeriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {

    @GetMapping("/imports")
    public ApiResponse<ImportSeriesResponse> imports(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam String importer,
            @RequestParam String exporter,
            @RequestParam String productSlug
    ) {
        // TODO: fetch from import_trade_monthly_fact
        return ApiResponse.ok(ImportSeriesResponse.builder().series(java.util.List.of()).build());
    }
}
