package org.example.part1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SinPowerSeriesTest {

    private static final double DELTA = 1e-5;
    private static final int N = 20;

    private static Stream<Arguments> provideReferenceAngles() {
        return Stream.of(
                Arguments.of(0.0),
                Arguments.of(Math.PI / 6),
                Arguments.of(Math.PI / 2),
                Arguments.of(-Math.PI / 2),
                Arguments.of(Math.PI),
                Arguments.of(2 * Math.PI)
        );
    }

    @ParameterizedTest(name = "x={0}")
    @MethodSource("provideReferenceAngles")
    void sinMatchesMathSinForReferenceAngles(double x) {
        SinPowerSeries sinPowerSeries = new SinPowerSeries();
        double actual = sinPowerSeries.sin(x, N);
        double expected = Math.sin(x);

        assertEquals(expected, actual, DELTA, "Failed for x: " + x);
    }

    @Test
    void sinWithZeroTermsReturnsFirstSeriesTerm() {
        SinPowerSeries sinPowerSeries = new SinPowerSeries();
        double x = 0.7;

        assertEquals(x, sinPowerSeries.sin(x, 0), DELTA);
    }

    private static Stream<Arguments> oddFunctionCases() {
        return Stream.of(
                Arguments.of(0.1),
                Arguments.of(0.5),
                Arguments.of(1.3)
        );
    }

    @ParameterizedTest(name = "x={0}")
    @MethodSource("oddFunctionCases")
    void sinIsOddFunction(double x) {
        SinPowerSeries sinPowerSeries = new SinPowerSeries();

        assertEquals(-sinPowerSeries.sin(x, N), sinPowerSeries.sin(-x, N), DELTA);
    }
}