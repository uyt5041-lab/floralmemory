package com.bit.floralmemory.util;

import java.util.List;

public final class StatsUtils {
    private StatsUtils() {}

    public static double mean(List<Double> xs) {
        if (xs == null || xs.isEmpty()) return 0.0;
        double s = 0.0;
        for (Double x : xs) {
            if (x != null) s += x;
        }
        return s / xs.size();
    }

    public static double stddev(List<Double> xs) {
        if (xs == null || xs.size() < 2) return 0.0;
        double m = mean(xs);
        double ss = 0.0;
        for (Double x : xs) {
            if (x == null) continue;
            double d = x - m;
            ss += d * d;
        }
        return Math.sqrt(ss / (xs.size() - 1));
    }
}
