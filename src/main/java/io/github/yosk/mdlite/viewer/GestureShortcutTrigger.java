package io.github.yosk.mdlite.viewer;

public final class GestureShortcutTrigger {
    private static final int DOUBLE_TAP = 1;
    private static final int CIRCLE = 2;
    private static final int CUSTOM_SHAPE = 3;
    private static final int SWIPE_LEFT = 4;
    private static final int SWIPE_RIGHT = 5;
    private static final int SWIPE_UP = 6;
    private static final int SWIPE_DOWN = 7;

    private static final String DOUBLE_TAP_VALUE = "double_tap";
    private static final String CIRCLE_VALUE = "circle";
    private static final String CUSTOM_SHAPE_VALUE = "custom_shape";
    private static final String SWIPE_LEFT_VALUE = "swipe_left";
    private static final String SWIPE_RIGHT_VALUE = "swipe_right";
    private static final String SWIPE_UP_VALUE = "swipe_up";
    private static final String SWIPE_DOWN_VALUE = "swipe_down";

    private final int value;

    private GestureShortcutTrigger(int value) {
        this.value = value;
    }

    public static GestureShortcutTrigger doubleTap() {
        return new GestureShortcutTrigger(DOUBLE_TAP);
    }

    public static GestureShortcutTrigger circle() {
        return new GestureShortcutTrigger(CIRCLE);
    }

    public static GestureShortcutTrigger customShape() {
        return new GestureShortcutTrigger(CUSTOM_SHAPE);
    }

    public static GestureShortcutTrigger swipeLeft() {
        return new GestureShortcutTrigger(SWIPE_LEFT);
    }

    public static GestureShortcutTrigger swipeRight() {
        return new GestureShortcutTrigger(SWIPE_RIGHT);
    }

    public static GestureShortcutTrigger swipeUp() {
        return new GestureShortcutTrigger(SWIPE_UP);
    }

    public static GestureShortcutTrigger swipeDown() {
        return new GestureShortcutTrigger(SWIPE_DOWN);
    }

    public static GestureShortcutTrigger fromStoredValue(String storedValue) {
        if (CIRCLE_VALUE.equals(storedValue)) {
            return circle();
        }
        if (CUSTOM_SHAPE_VALUE.equals(storedValue)) {
            return customShape();
        }
        if (SWIPE_LEFT_VALUE.equals(storedValue)) {
            return swipeLeft();
        }
        if (SWIPE_RIGHT_VALUE.equals(storedValue)) {
            return swipeRight();
        }
        if (SWIPE_UP_VALUE.equals(storedValue)) {
            return swipeUp();
        }
        if (SWIPE_DOWN_VALUE.equals(storedValue)) {
            return swipeDown();
        }
        return doubleTap();
    }

    public boolean isDoubleTap() {
        return value == DOUBLE_TAP;
    }

    public boolean isCircle() {
        return value == CIRCLE;
    }

    public boolean isCustomShape() {
        return value == CUSTOM_SHAPE;
    }

    public boolean isSwipeLeft() {
        return value == SWIPE_LEFT;
    }

    public boolean isSwipeRight() {
        return value == SWIPE_RIGHT;
    }

    public boolean isSwipeUp() {
        return value == SWIPE_UP;
    }

    public boolean isSwipeDown() {
        return value == SWIPE_DOWN;
    }

    public String storedValue() {
        if (isCircle()) {
            return CIRCLE_VALUE;
        }
        if (isCustomShape()) {
            return CUSTOM_SHAPE_VALUE;
        }
        if (isSwipeLeft()) {
            return SWIPE_LEFT_VALUE;
        }
        if (isSwipeRight()) {
            return SWIPE_RIGHT_VALUE;
        }
        if (isSwipeUp()) {
            return SWIPE_UP_VALUE;
        }
        if (isSwipeDown()) {
            return SWIPE_DOWN_VALUE;
        }
        return DOUBLE_TAP_VALUE;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof GestureShortcutTrigger
                && value == ((GestureShortcutTrigger) other).value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
