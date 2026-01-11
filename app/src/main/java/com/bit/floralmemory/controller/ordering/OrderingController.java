package com.bit.floralmemory.controller.ordering;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.ordering.*;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordering")
public class OrderingController {

    private final com.bit.floralmemory.service.OrderingService orderingService;

    public OrderingController(com.bit.floralmemory.service.OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    @PostMapping("/run")
    public ApiResponse<OrderingRunResponse> run(@RequestBody OrderingRunRequest req) {
        Long id = orderingService.runOrdering(req);
        return ApiResponse.ok(OrderingRunResponse.builder().orderingRunId(id).build());
    }

    @PostMapping("/policy-sweep")
    public ApiResponse<PolicySweepResponse> policySweep(@RequestBody PolicySweepRequest req) {
        PolicySweepResponse resp = orderingService.runPolicySweep(req);
        return ApiResponse.ok(resp);
    }

    @GetMapping("/results")
    public ApiResponse<OrderingResultsResponse> results(@RequestParam Long orderingRunId) {
        // TODO
        return ApiResponse
                .ok(OrderingResultsResponse.builder().orderingRunId(orderingRunId).series(java.util.List.of()).build());
    }
}
