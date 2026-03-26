package org.example.part3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BinarySunriseEventTest {

    private BinarySunriseEvent event;

    @BeforeEach
    void setUp() {
        event = new BinarySunriseEvent();
    }

    @Test
    void testInitialState() {
        assertEquals(BinarySunriseEvent.IlluminationState.TOTAL_DARKNESS, event.getIlluminationState());
        assertEquals(BinarySunriseEvent.HorizonState.PITCH_BLACK, event.getHorizonState());
        assertEquals(BinarySunriseEvent.AtmosphereState.RAREFIED, event.getAtmosphereState());
    }

    @Test
    void testSuccessfulStateTransitions() {
        // 1. Сверкает точка
        event.flashPointOfLight();
        assertEquals(BinarySunriseEvent.IlluminationState.BRIGHT_POINT, event.getIlluminationState());
        assertEquals(BinarySunriseEvent.HorizonState.PITCH_BLACK, event.getHorizonState(), "Горизонт пока не горит");

        // 2. Расползается в полумесяц
        event.expandIntoCrescent();
        assertEquals(BinarySunriseEvent.IlluminationState.NARROW_CRESCENT, event.getIlluminationState());

        // 3. Появляются два солнца
        event.revealSuns();
        assertEquals(BinarySunriseEvent.IlluminationState.TWO_SUNS, event.getIlluminationState());
        assertEquals(BinarySunriseEvent.HorizonState.BURNING_WHITE_FLAME, event.getHorizonState());
        assertEquals(BinarySunriseEvent.AtmosphereState.COLORFUL_FLASHES, event.getAtmosphereState());
    }

    @Test
    void testFlashPointOfLightThrowsExceptionIfInvalidState() {
        event.flashPointOfLight(); // Переводим в BRIGHT_POINT
        // Пытаемся вызвать повторно
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> event.flashPointOfLight());
        assertEquals("Точка света может сверкнуть только из полной темноты.", exception.getMessage());
    }

    @Test
    void testExpandIntoCrescentThrowsExceptionIfInDarkness() {
        // Пытаемся расширить темноту в полумесяц
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> event.expandIntoCrescent());
        assertEquals("В полумесяц может превратиться только яркая точка.", exception.getMessage());
    }

    @Test
    void testExpandIntoCrescentThrowsExceptionIfAlreadyCrescent() {
        event.flashPointOfLight();
        event.expandIntoCrescent();
        // Пытаемся расширить полумесяц в полумесяц
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> event.expandIntoCrescent());
        assertEquals("В полумесяц может превратиться только яркая точка.", exception.getMessage());
    }

    @Test
    void testRevealSunsThrowsExceptionIfInDarkness() {
        // Пытаемся показать солнца сразу из темноты
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> event.revealSuns());
        assertEquals("Солнца могут появиться только после фазы узкого полумесяца.", exception.getMessage());
    }

    @Test
    void testRevealSunsThrowsExceptionIfFromBrightPoint() {
        event.flashPointOfLight();
        // Пытаемся показать солнца из точки света, минуя полумесяц
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> event.revealSuns());
        assertEquals("Солнца могут появиться только после фазы узкого полумесяца.", exception.getMessage());
    }

    // Опциональный тест: проверка значений Enum для 100% формального покрытия байткода (jacoco иногда просит)
    @Test
    void testEnumValues() {
        assertNotNull(BinarySunriseEvent.IlluminationState.valueOf("TOTAL_DARKNESS"));
        assertNotNull(BinarySunriseEvent.HorizonState.valueOf("PITCH_BLACK"));
        assertNotNull(BinarySunriseEvent.AtmosphereState.valueOf("RAREFIED"));
    }
}
