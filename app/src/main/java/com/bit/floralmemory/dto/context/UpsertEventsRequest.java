package com.bit.floralmemory.dto.context;

import java.time.LocalDate;
import java.util.List;

public class UpsertEventsRequest {
    private List<EventDto> events;

    public UpsertEventsRequest() {
    }

    public UpsertEventsRequest(List<EventDto> events) {
        this.events = events;
    }

    public List<EventDto> getEvents() {
        return events;
    }

    public void setEvents(List<EventDto> events) {
        this.events = events;
    }

    public static class EventDto {
        private LocalDate date;
        private String code;
        private int intensity;
        private String notes;

        public EventDto() {
        }

        public EventDto(LocalDate date, String code, int intensity, String notes) {
            this.date = date;
            this.code = code;
            this.intensity = intensity;
            this.notes = notes;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getIntensity() {
            return intensity;
        }

        public void setIntensity(int intensity) {
            this.intensity = intensity;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}
