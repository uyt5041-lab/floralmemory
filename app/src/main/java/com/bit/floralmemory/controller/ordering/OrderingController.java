package com.bit.floralmemory.controller.ordering;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.ordering.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordering")
@RequiredArgsConstructor
public class OrderingController {

    @PostMapping("/run")
    public ApiResponse<OrderingRunResponse> run(@RequestBody OrderingRunRequest req) {
        // TODO: create ordering_run + cost_assumption + order_recommendation
        return ApiResponse.ok(OrderingRunResponse.builder().orderingRunId(0L).build());
    }

    @GetMapping("/results")
    public ApiResponse<OrderingResultsResponse> results(@RequestParam Long orderingRunId) {
        // TODO
        return ApiResponse.ok(OrderingResultsResponse.builder().orderingRunId(orderingRunId).series(java.util.List.of()).build());
    }
}
