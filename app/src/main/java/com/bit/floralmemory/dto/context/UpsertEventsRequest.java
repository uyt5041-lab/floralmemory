package com.bit.floralmemory.dto.context;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class UpsertEventsRequest {
    private List<EventDto> events;

    @Data
    public static class EventDto {
        private LocalDate date;
        private String code;
        private int intensity;
        private String notes;
    }
}
