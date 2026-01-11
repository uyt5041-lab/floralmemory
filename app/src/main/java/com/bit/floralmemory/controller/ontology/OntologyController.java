package com.bit.floralmemory.controller.ontology;

import com.bit.floralmemory.dto.common.ApiResponse;
import com.bit.floralmemory.dto.ontology.UpsertMappingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ontology")
@RequiredArgsConstructor
public class OntologyController {

    @GetMapping("/mappings")
    public ApiResponse<Object> list(@RequestParam String productSlug) {
        // TODO
        return ApiResponse.ok(null);
    }

    @PostMapping("/mappings")
    public ApiResponse<Object> upsert(@RequestBody UpsertMappingRequest req) {
        // TODO
        return ApiResponse.ok(java.util.Map.of("updated", true));
    }
}
