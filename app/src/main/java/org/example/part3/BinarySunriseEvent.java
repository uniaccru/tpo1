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

    /**
     * "В полной темноте сверкнула ослепительно яркая точка света."
     */
    public void flashPointOfLight() {
        if (this.illuminationState != IlluminationState.TOTAL_DARKNESS) {
            throw new IllegalStateException("Точка света может сверкнуть только из полной темноты.");
        }
        this.illuminationState = IlluminationState.BRIGHT_POINT;
    }

    /**
     * "Она начала расползаться в стороны, превращаясь в узкий полумесяц..."
     */
    public void expandIntoCrescent() {
        if (this.illuminationState != IlluminationState.BRIGHT_POINT) {
            throw new IllegalStateException("В полумесяц может превратиться только яркая точка.");
        }
        this.illuminationState = IlluminationState.NARROW_CRESCENT;
    }

    /**
     * "...через несколько секунд показались два солнца: огненные светила,
     * сжигающие белым пламенем черный край горизонта. Яркие цветные сполохи
     * струились сквозь разреженную атмосферу."
     */
    public void revealSuns() {
        if (this.illuminationState != IlluminationState.NARROW_CRESCENT) {
            throw new IllegalStateException("Солнца могут появиться только после фазы узкого полумесяца.");
        }
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