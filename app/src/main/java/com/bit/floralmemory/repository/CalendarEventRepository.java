package com.bit.floralmemory.repository;

import com.bit.floralmemory.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    Optional<CalendarEvent> findByEventDateAndEventCode(LocalDate date, String code);
}
