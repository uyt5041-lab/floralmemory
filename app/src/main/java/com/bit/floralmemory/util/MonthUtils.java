package com.bit.floralmemory.util;

import java.time.LocalDate;

public final class MonthUtils {
    private MonthUtils() {}

    public static LocalDate normalizeToMonthStart(LocalDate date) {
        if (date == null) return null;
        return LocalDate.of(date.getYear(), date.getMonth(), 1);
    }
}
