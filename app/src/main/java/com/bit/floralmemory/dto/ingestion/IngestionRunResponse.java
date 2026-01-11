package com.bit.floralmemory.dto.ingestion;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IngestionRunResponse {
    private Long ingestionId;
    private String status;
}
