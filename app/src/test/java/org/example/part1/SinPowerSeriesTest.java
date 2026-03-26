package org.example.part1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SinPowerSeriesTest {

    private static final double DELTA = 1e-5;
    private static final int N = 20;

    // Метод, поставляющий данные (замена @Parameterized.Parameters)
    private static Stream<Arguments> provideDataForSin() {
        return Stream.of(
                Arguments.of(0.0, 0.0),
                Arguments.of(Math.PI / 6, 0.5),
                Arguments.of(Math.PI / 4, 0.70711),
                Arguments.of(Math.PI / 3, 0.86603),
                Arguments.of(Math.PI / 2, 1.0),
                Arguments.of(-Math.PI / 6, -0.5),
                Arguments.of(-Math.PI / 2, -1.0),
                Arguments.of(Math.PI, 0.0),
                Arguments.of(3 * Math.PI / 2, -1.0),
                Arguments.of(2 * Math.PI, 0.0),
                Arguments.of(0.1, 0.09983),
                Arguments.of(0.5, 0.47943)
        );
    }

    // Сам тест (замена @Test и полей класса)
    @ParameterizedTest(name = "x={0}, expected={1}")
    @MethodSource("provideDataForSin")
    void sinParameterized(double x, double expected) {
        SinPowerSeries sinPowerSeries = new SinPowerSeries();
        double actual = sinPowerSeries.sin(x, N);

        assertEquals(expected, actual, DELTA, "Failed for x: " + x);
    }
}