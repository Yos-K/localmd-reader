package io.github.yosk.mdlite.viewer;

public final class CircleGesturePath {
    private static final float MIN_SIZE_PX = 72f;
    private static final float MAX_ASPECT_RATIO = 1.8f;
    private static final float MAX_CLOSE_DISTANCE_RATIO = 0.75f;
    private static final float MIN_TRAVEL_RATIO = 2.0f;

    private final float firstX;
    private final float firstY;
    private final float lastX;
    private final float lastY;
    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;
    private final float travelDistance;
    private final int pointCount;

    private CircleGesturePath(
            float firstX,
            float firstY,
            float lastX,
            float lastY,
            float minX,
            float maxX,
            float minY,
            float maxY,
            float travelDistance,
            int pointCount) {
        this.firstX = firstX;
        this.firstY = firstY;
        this.lastX = lastX;
        this.lastY = lastY;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.travelDistance = travelDistance;
        this.pointCount = pointCount;
    }

    public static CircleGesturePath empty() {
        return new CircleGesturePath(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0);
    }

    public static CircleGesturePath fromPoints(float[] xs, float[] ys) {
        if (xs == null || ys == null || xs.length == 0 || xs.length != ys.length) {
            return empty();
        }
        float minX = xs[0];
        float maxX = xs[0];
        float minY = ys[0];
        float maxY = ys[0];
        float distance = 0f;
        for (int i = 1; i < xs.length; i++) {
            minX = Math.min(minX, xs[i]);
            maxX = Math.max(maxX, xs[i]);
            minY = Math.min(minY, ys[i]);
            maxY = Math.max(maxY, ys[i]);
            distance += distance(xs[i - 1], ys[i - 1], xs[i], ys[i]);
        }
        return new CircleGesturePath(xs[0], ys[0], xs[xs.length - 1], ys[ys.length - 1], minX, maxX, minY, maxY, distance, xs.length);
    }

    public boolean isCircleLike() {
        if (pointCount < 6) {
            return false;
        }
        float width = maxX - minX;
        float height = maxY - minY;
        float size = Math.max(width, height);
        if (size < MIN_SIZE_PX) {
            return false;
        }
        float aspectRatio = Math.max(width, height) / Math.max(1f, Math.min(width, height));
        if (aspectRatio > MAX_ASPECT_RATIO) {
            return false;
        }
        if (distance(firstX, firstY, lastX, lastY) > size * MAX_CLOSE_DISTANCE_RATIO) {
            return false;
        }
        return travelDistance >= size * MIN_TRAVEL_RATIO;
    }

    private static float distance(float startX, float startY, float endX, float endY) {
        float dx = endX - startX;
        float dy = endY - startY;
        return (float) Math.sqrt((dx * dx) + (dy * dy));
    }
}
