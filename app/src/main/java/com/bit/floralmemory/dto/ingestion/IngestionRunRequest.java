package com.bit.floralmemory.dto.ingestion;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IngestionRunRequest {
    private String source;
    private LocalDate fromMonth;
    private LocalDate toMonth;
    private Scope scope;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Scope {
        private String importer;
        private String exporter;
        private List<String> hsCodes;
    }
}
