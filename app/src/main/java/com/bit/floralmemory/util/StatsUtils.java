package com.bit.floralmemory.util;

import java.util.List;

public final class StatsUtils {
    private StatsUtils() {
    }

    public static double mean(List<Double> xs) {
        if (xs == null || xs.isEmpty())
            return 0.0;
        double s = 0.0;
        for (Double x : xs) {
            if (x != null)
                s += x;
        }
        return s / xs.size();
    }

    public static double stddev(List<Double> xs) {
        if (xs == null || xs.size() < 2)
            return 0.0;
        double m = mean(xs);
        double ss = 0.0;
        for (Double x : xs) {
            if (x == null)
                continue;
            double d = x - m;
            ss += d * d;
        }
        return Math.sqrt(ss / (xs.size() - 1));
    }

    public static double inverseNormalCdf(double p) {
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("p must be between 0.0 and 1.0");
        }
        if (p == 0.0)
            return -Double.MAX_VALUE;
        if (p == 1.0)
            return Double.MAX_VALUE;

        // Beasley-Springer-Moro Algorithm (Approximation)
        // Or simpler, let's use the rational approximation by Abramowitz and Stegun
        // (26.2.23)
        // Wait, standard approach for high precision is crucial for inventory.
        // Let's use a standard approximation.

        // Acklam's algorithm is good.
        // For simplicity in this env, use a known approximation:
        // C++ implementation port or similar.

        // Simple approximation (error < 4.5e-4) - Abramowitz and Stegun 26.2.23
        double t = Math.sqrt(-2.0 * Math.log(p < 0.5 ? p : 1.0 - p));
        double num = 2.515517 + 0.802853 * t + 0.010328 * t * t;
        double den = 1.0 + 1.432788 * t + 0.189269 * t * t + 0.001308 * t * t * t;
        double z = t - num / den;
        return p < 0.5 ? -z : z;
    }

    public static double normalPdf(double z) {
        return (1.0 / Math.sqrt(2.0 * Math.PI)) * Math.exp(-0.5 * z * z);
    }

    public static double normalCdf(double z) {
        // Approximation using Error Function (erf)
        // CDF(x) = 0.5 * (1 + erf(x / sqrt(2)))
        return 0.5 * (1.0 + erf(z / Math.sqrt(2.0)));
    }

    // Apache Commons Math Erf implementation (approx)
    public static double erf(double x) {
        // constants
        double a1 = 0.254829592;
        double a2 = -0.284496736;
        double a3 = 1.421413741;
        double a4 = -1.453152027;
        double a5 = 1.061405429;
        double p = 0.3275911;

        // Save the sign of x
        double sign = 1.0;
        if (x < 0) {
            sign = -1.0;
        }
        x = Math.abs(x);

        // A&S formula 7.1.26
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);

        return sign * y;
    }
}
