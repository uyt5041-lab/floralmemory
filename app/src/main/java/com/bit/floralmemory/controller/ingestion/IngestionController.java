package com.bit.floralmemory.controller.ingestion;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.ingestion.IngestionRunRequest;
import com.bit.floralmemory.dto.ingestion.IngestionRunResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
public class IngestionController {

    @PostMapping("/run")
    public ApiResponse<IngestionRunResponse> run(@RequestBody IngestionRunRequest req) {
        // TODO: validate + start ingestion job
        return ApiResponse.ok(IngestionRunResponse.builder()
                .ingestionId(0L)
                .status("RUNNING")
                .build());
    }

    @GetMapping("/{ingestionId}")
    public ApiResponse<Object> status(@PathVariable Long ingestionId) {
        // TODO: return ingestion job status
        return ApiResponse.ok(null);
    }
}
