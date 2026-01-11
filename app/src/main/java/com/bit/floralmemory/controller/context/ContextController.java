package com.bit.floralmemory.controller.context;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.context.RebuildContextRequest;
import com.bit.floralmemory.dto.context.UpsertEventsRequest;
import com.bit.floralmemory.service.ContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/context")
@RequiredArgsConstructor
public class ContextController {

    private final ContextService contextService;

    @PostMapping("/events")
    public ApiResponse<Map<String, Integer>> upsertEvents(@RequestBody UpsertEventsRequest req) {
        int count = contextService.upsertEvents(req);
        return ApiResponse.ok(Map.of("upserted", count));
    }

    @PostMapping("/rebuild")
    public ApiResponse<Map<String, Integer>> rebuild(@RequestBody RebuildContextRequest req) {
        int count = contextService.rebuildContext(req);
        return ApiResponse.ok(Map.of("monthsBuilt", count));
    }
}
