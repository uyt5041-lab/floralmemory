package com.bit.floralmemory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "calendar_events")
public class CalendarEvent {
    @Id
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_code", nullable = false)
    private String eventCode;

    @Column(name = "intensity", nullable = false)
    private Integer intensity; // 1-10

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public CalendarEvent() {
    }

    public CalendarEvent(LocalDate eventDate, String eventCode, Integer intensity, String notes,
            OffsetDateTime createdAt) {
        this.eventDate = eventDate;
        this.eventCode = eventCode;
        this.intensity = intensity;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public static CalendarEventBuilder builder() {
        return new CalendarEventBuilder();
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Integer getIntensity() {
        return intensity;
    }

    public void setIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class CalendarEventBuilder {
        private LocalDate eventDate;
        private String eventCode;
        private Integer intensity;
        private String notes;
        private OffsetDateTime createdAt;

        CalendarEventBuilder() {
        }

        public CalendarEventBuilder eventDate(LocalDate eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public CalendarEventBuilder eventCode(String eventCode) {
            this.eventCode = eventCode;
            return this;
        }

        public CalendarEventBuilder intensity(Integer intensity) {
            this.intensity = intensity;
            return this;
        }

        public CalendarEventBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public CalendarEventBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CalendarEvent build() {
            return new CalendarEvent(eventDate, eventCode, intensity, notes, createdAt);
        }
    }
}
