package io.github.yosk.mdlite.viewer;

public final class CustomGestureShapeMatcher {
    private static final float MAX_AVERAGE_DISTANCE = 0.05f;

    private final CustomGestureShape registeredShape;

    private CustomGestureShapeMatcher(CustomGestureShape registeredShape) {
        if (registeredShape == null) {
            throw new IllegalArgumentException("registered custom gesture shape is required");
        }
        this.registeredShape = registeredShape;
    }

    public static CustomGestureShapeMatcher forShape(CustomGestureShape registeredShape) {
        return new CustomGestureShapeMatcher(registeredShape);
    }

    public boolean matches(CustomGestureShape inputShape) {
        return registeredShape.averageDistanceTo(inputShape) <= MAX_AVERAGE_DISTANCE;
    }
}
