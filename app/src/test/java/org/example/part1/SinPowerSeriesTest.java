package org.example.part1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class SinPowerSeriesTest {

    private static final double DELTA = 1e-5;
    private static final double DERIVATIVE_DELTA = 1e-4;
    private static final double STEP = 1e-6;
    private static final int N = 20;
    private final SinPowerSeries sinPS = new SinPowerSeries();

    private static double derivativeAt(SinPowerSeries sinPS, double x, int terms) {
        return (sinPS.sin(x + STEP, terms) - sinPS.sin(x - STEP, terms)) / (2 * STEP);
    }

    private static double secondDerivativeAt(SinPowerSeries sinPS, double x, int terms) {
        return (sinPS.sin(x + STEP, terms) - 2 * sinPS.sin(x, terms) + sinPS.sin(x - STEP, terms))
                / (STEP * STEP);
    }

    // СВОЙСТВО: sin(0) = 0 (начало координат)
    @Test
    void sinAtZeroIsZero() {
        assertEquals(0.0, sinPS.sin(0.0, N), DELTA);
    }

    // СВОЙСТВО: нечётная функция — sin(-x) = -sin(x)
    @ParameterizedTest(name = "x={0}")
    @ValueSource(doubles = {0.1, 0.5, 1.3, Math.PI / 4})
    void sinIsOddFunction(double x) {
        assertEquals(-sinPS.sin(x, N), sinPS.sin(-x, N), DELTA);
    }

    // СВОЙСТВО: периодичность — sin(x + 2π) = sin(x)
    @ParameterizedTest(name = "x={0}")
    @ValueSource(doubles = {0.0, 0.1, Math.PI / 6, Math.PI / 2, 1.0, 2.0})
    void sinIsPeriodic(double x) {
        assertEquals(sinPS.sin(x, N), sinPS.sin(x + 2 * Math.PI, N), DELTA);
    }

    // СВОЙСТВО: sin достигает максимума в π/2
    @Test
    void sinHasMaximumAtHalfPi() {
        assertEquals(0.0, derivativeAt(sinPS, Math.PI / 2, N), DERIVATIVE_DELTA);
        assertTrue(secondDerivativeAt(sinPS, Math.PI / 2, N) < 0.0);
    }

    // СВОЙСТВО: sin достигает минимума в -π/2
    @Test
    void sinHasMinimumAtNegativeHalfPi() {
        assertEquals(0.0, derivativeAt(sinPS, -Math.PI / 2, N), DERIVATIVE_DELTA);
        assertTrue(secondDerivativeAt(sinPS, -Math.PI / 2, N) > 0.0);
    }

    // СВОЙСТВО: sin(x) = cos(π/2 - x) — дополнение до π/2
    @ParameterizedTest(name = "x={0}")
    @ValueSource(doubles = {0.2, 0.7, 1.1, Math.PI / 4})
    void sinEqualsCosinOfComplement(double x) {
        // cos через формулу sin(π/2 - x) можно выразить через тот же синус
        // sin(x) = sin(π/2 - (π/2 - x)) — тавтология, лучше сравним с Math.cos
        assertEquals(Math.cos(Math.PI / 2 - x), sinPS.sin(x, N), DELTA);
    }

    // СВОЙСТВО: ряд с нулём членов возвращает только первый член — x
    @Test
    void sinWithZeroTermsReturnsFirstSeriesTerm() {
        double x = 0.7;
        assertEquals(x, sinPS.sin(x, 0), DELTA);
    }

    // СВОЙСТВО: результат совпадает с Math.sin на произвольных значениях
    private static Stream<Double> arbitraryAngles() {
        return Stream.of(-2.5, -1.0, -0.3, 0.3, 1.0, 2.5, 3.0);
    }

    @ParameterizedTest(name = "x={0}")
    @MethodSource("arbitraryAngles")
    void sinMatchesMathSin(double x) {
        assertEquals(Math.sin(x), sinPS.sin(x, N), DELTA);
    }
}