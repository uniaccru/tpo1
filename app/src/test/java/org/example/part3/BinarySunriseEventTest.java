package org.example.part3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BinarySunriseEventTest {

    private BinarySunriseEvent event;

    @BeforeEach
    void setUp() {
        event = new BinarySunriseEvent();
    }

    @FunctionalInterface
    private interface EventAction {
        void apply(BinarySunriseEvent event);
    }

    @Test
    void testInitialState() {
        // Начальное состояние.
        assertEquals(BinarySunriseEvent.IlluminationState.TOTAL_DARKNESS, event.getIlluminationState());
        assertEquals(BinarySunriseEvent.HorizonState.PITCH_BLACK, event.getHorizonState());
        assertEquals(BinarySunriseEvent.AtmosphereState.RAREFIED, event.getAtmosphereState());
    }

    @Test
    void flashPointOfLightUpdatesOnlyIllumination() {
        // Переход в яркую точку.
        event.flashPointOfLight();

        assertEquals(BinarySunriseEvent.IlluminationState.BRIGHT_POINT, event.getIlluminationState());
        assertEquals(BinarySunriseEvent.HorizonState.PITCH_BLACK, event.getHorizonState());
        assertEquals(BinarySunriseEvent.AtmosphereState.RAREFIED, event.getAtmosphereState());
    }

    @Test
    void expandIntoCrescentUpdatesOnlyIllumination() {
        // Переход в полумесяц.
        event.flashPointOfLight();
        event.expandIntoCrescent();

        assertEquals(BinarySunriseEvent.IlluminationState.NARROW_CRESCENT, event.getIlluminationState());
        assertEquals(BinarySunriseEvent.HorizonState.PITCH_BLACK, event.getHorizonState());
        assertEquals(BinarySunriseEvent.AtmosphereState.RAREFIED, event.getAtmosphereState());
    }

    @Test
    void revealSunsUpdatesAllVisualStates() {
        // Появление двух солнц.
        event.flashPointOfLight();
        event.expandIntoCrescent();
        event.revealSuns();

        assertEquals(BinarySunriseEvent.IlluminationState.TWO_SUNS, event.getIlluminationState());
        assertEquals(BinarySunriseEvent.HorizonState.BURNING_WHITE_FLAME, event.getHorizonState());
        assertEquals(BinarySunriseEvent.AtmosphereState.COLORFUL_FLASHES, event.getAtmosphereState());
    }

    private static Stream<Arguments> invalidTransitionCases() {
        // Невалидные переходы.
        return Stream.of(
                Arguments.of(
                        "flashPoint повторно",
                        (EventAction) BinarySunriseEvent::flashPointOfLight,
                        (EventAction) BinarySunriseEvent::flashPointOfLight,
                        "Точка света может сверкнуть только из полной темноты."
                ),
                Arguments.of(
                        "expand из темноты",
                        (EventAction) e -> {
                        },
                        (EventAction) BinarySunriseEvent::expandIntoCrescent,
                        "В полумесяц может превратиться только яркая точка."
                ),
                Arguments.of(
                        "expand повторно",
                        (EventAction) e -> {
                            e.flashPointOfLight();
                            e.expandIntoCrescent();
                        },
                        (EventAction) BinarySunriseEvent::expandIntoCrescent,
                        "В полумесяц может превратиться только яркая точка."
                ),
                Arguments.of(
                        "reveal из темноты",
                        (EventAction) e -> {
                        },
                        (EventAction) BinarySunriseEvent::revealSuns,
                        "Солнца могут появиться только после фазы узкого полумесяца."
                ),
                Arguments.of(
                        "reveal из яркой точки",
                        (EventAction) BinarySunriseEvent::flashPointOfLight,
                        (EventAction) BinarySunriseEvent::revealSuns,
                        "Солнца могут появиться только после фазы узкого полумесяца."
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidTransitionCases")
    void invalidTransitionsThrow(
            String scenario,
            EventAction arrange,
            EventAction action,
            String expectedMessage
    ) {
        arrange.apply(event);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> action.apply(event));
        assertEquals(expectedMessage, exception.getMessage(), scenario);
    }

}
