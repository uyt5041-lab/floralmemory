package com.bit.floralmemory.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatsUtilsTest {

    @Test
    void testNormalPdf() {
        // PDF at 0 for standard normal is 1/sqrt(2pi) approx 0.3989
        assertEquals(0.3989, StatsUtils.normalPdf(0), 0.0001);
        // Symmetric
        assertEquals(StatsUtils.normalPdf(1.0), StatsUtils.normalPdf(-1.0), 0.0000001);
    }

    @Test
    void testNormalCdf() {
        assertEquals(0.5, StatsUtils.normalCdf(0), 0.0001);
        assertEquals(0.8413, StatsUtils.normalCdf(1.0), 0.0001); // 1 sigma
        assertEquals(0.9772, StatsUtils.normalCdf(2.0), 0.0001); // 2 sigma
    }

    @Test
    void testInverseNormalCdf() {
        assertEquals(0.0, StatsUtils.inverseNormalCdf(0.5), 0.0001);
        assertEquals(1.645, StatsUtils.inverseNormalCdf(0.95), 0.001); // 95% confidence (one-sided) approx 1.645
        assertEquals(1.96, StatsUtils.inverseNormalCdf(0.975), 0.001); // 97.5% (two-sided 95%) approx 1.96
    }
}
