package org.example.part1;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SinPowerSeriesTest {

    private static final double DELTA = 1e-9;
    private static final int N = 30;
    private final SinPowerSeries sinPS = new SinPowerSeries();

    private double f(double x) {
        return sinPS.sin(x, N);
    }

    @Test
    void sinAtZeroIsZero() {
        assertEquals(0.0, f(0), DELTA);
    }

    @ParameterizedTest(name = "x={0}")
    @ValueSource(doubles = {0.1, 0.5, 1.3, 2.8})
    void isOdd(double x) {
        assertEquals(-f(x), f(-x), DELTA);
    }

    @ParameterizedTest(name = "x={0}")
    @ValueSource(doubles = {-3.0, -1.0, 0.0, 0.7, 2.0, 3.0})
    void hasPeriodTwoPi(double x) {
        assertEquals(f(x), f(x + 2 * Math.PI), DELTA);
        assertEquals(f(x), f(x - 2 * Math.PI), DELTA);
    }

    @Test
    void piIsNotAPeriod() {
        double x = Math.PI / 2;
        assertNotEquals(f(x), f(x + Math.PI), DELTA);
    }

    @Test
    void maximumAtHalfPi() {
        double x = Math.PI / 2;
        assertEquals(1.0, f(x), DELTA); //значение в максимуме
        assertTrue(f(x - 0.1) < 1.0);
        assertTrue(f(x + 0.1) < 1.0);
    }

    @Test
    void minimumAtNegativeHalfPi() {
        double x = -Math.PI / 2;
        assertEquals(-1.0, f(x), DELTA);
        assertTrue(f(x - 0.1) > -1.0);
        assertTrue(f(x + 0.1) > -1.0);
    }

    @Test
    void negativeNumberOfTermsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> sinPS.sin(1.0, -1));
    }

    @Test
    void nanGivesNan() {
        assertTrue(Double.isNaN(f(Double.NaN)));
    }
}