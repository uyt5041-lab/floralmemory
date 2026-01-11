package com.bit.floralmemory.controller.reports;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.reports.ModelComparisonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {

    @GetMapping("/models")
    public ApiResponse<ModelComparisonResponse> modelComparison(@RequestParam Long runId) {
        // TODO: return model_metric rows for runId
        return ApiResponse.ok(ModelComparisonResponse.builder().runId(runId).rows(java.util.List.of()).build());
    }
}
