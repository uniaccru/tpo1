package org.example.part3;
public class BinarySunriseEvent {

    public enum IlluminationState {
        TOTAL_DARKNESS,
        BRIGHT_POINT,
        NARROW_CRESCENT,
        TWO_SUNS
    }

    public enum HorizonState {
        PITCH_BLACK,
        BURNING_WHITE_FLAME
    }

    public enum AtmosphereState {
        RAREFIED,
        COLORFUL_FLASHES
    }

    private IlluminationState illuminationState;
    private HorizonState horizonState;
    private AtmosphereState atmosphereState;

    public BinarySunriseEvent() {
        this.illuminationState = IlluminationState.TOTAL_DARKNESS;
        this.horizonState = HorizonState.PITCH_BLACK;
        this.atmosphereState = AtmosphereState.RAREFIED;
    }

    private void requireIlluminationState(IlluminationState expected, String message) {
        if (this.illuminationState != expected) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * "В полной темноте сверкнула ослепительно яркая точка света."
     */
    public void flashPointOfLight() {
        requireIlluminationState(
                IlluminationState.TOTAL_DARKNESS,
                "Точка света может сверкнуть только из полной темноты."
        );
        this.illuminationState = IlluminationState.BRIGHT_POINT;
    }

    /**
     * "Она начала расползаться в стороны, превращаясь в узкий полумесяц..."
     */
    public void expandIntoCrescent() {
        requireIlluminationState(
                IlluminationState.BRIGHT_POINT,
                "В полумесяц может превратиться только яркая точка."
        );
        this.illuminationState = IlluminationState.NARROW_CRESCENT;
    }

    /**
     * "...через несколько секунд показались два солнца: огненные светила,
     * сжигающие белым пламенем черный край горизонта. Яркие цветные сполохи
     * струились сквозь разреженную атмосферу."
     */
    public void revealSuns() {
        requireIlluminationState(
                IlluminationState.NARROW_CRESCENT,
                "Солнца могут появиться только после фазы узкого полумесяца."
        );
        this.illuminationState = IlluminationState.TWO_SUNS;
        this.horizonState = HorizonState.BURNING_WHITE_FLAME;
        this.atmosphereState = AtmosphereState.COLORFUL_FLASHES;
    }

    // --- Getters ---

    public IlluminationState getIlluminationState() {
        return illuminationState;
    }

    public HorizonState getHorizonState() {
        return horizonState;
    }

    public AtmosphereState getAtmosphereState() {
        return atmosphereState;
    }
}