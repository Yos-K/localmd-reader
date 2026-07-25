package io.github.yosk.mdlite.viewer;

public enum HorizontalSwipe {
    NONE,
    LEFT,
    RIGHT;

    public static HorizontalSwipe from(float startX, float endX, float minimumDistance) {
        if (Float.isNaN(startX) || Float.isNaN(endX) || Float.isNaN(minimumDistance)
                || Float.isInfinite(startX) || Float.isInfinite(endX)
                || Float.isInfinite(minimumDistance) || minimumDistance <= 0f) {
            throw new IllegalArgumentException("horizontal swipe inputs must be finite and valid");
        }
        float delta = endX - startX;
        if (delta >= minimumDistance) {
            return RIGHT;
        }
        if (delta <= -minimumDistance) {
            return LEFT;
        }
        return NONE;
    }
}
