package io.github.yosk.mdlite.domain;

public final class ControlsPlacement {
    public static final String TOP_VALUE = "top";
    public static final String BOTTOM_VALUE = "bottom";

    private final String value;

    private ControlsPlacement(String value) {
        if (!TOP_VALUE.equals(value) && !BOTTOM_VALUE.equals(value)) {
            throw new IllegalArgumentException("unknown controls placement");
        }
        this.value = value;
    }

    public static ControlsPlacement top() {
        return new ControlsPlacement(TOP_VALUE);
    }

    public static ControlsPlacement bottom() {
        return new ControlsPlacement(BOTTOM_VALUE);
    }

    public static ControlsPlacement fromStoredValue(String value) {
        if (BOTTOM_VALUE.equals(value)) {
            return bottom();
        }
        return top();
    }

    public ControlsPlacement toggled() {
        if (isBottom()) {
            return top();
        }
        return bottom();
    }

    public boolean isBottom() {
        return BOTTOM_VALUE.equals(value);
    }

    public String storedValue() {
        return value;
    }
}
