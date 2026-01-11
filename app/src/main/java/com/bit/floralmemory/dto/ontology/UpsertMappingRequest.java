package com.bit.floralmemory.dto.ontology;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpsertMappingRequest {
    private String hsCode;
    private String productSlug;
    private Double confidence;
    private String source;
}
