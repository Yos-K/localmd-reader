package io.github.yosk.mdlite.viewer;

// Axis-aligned bounding box of a gesture's points (dp). Shared by
// CustomGestureShape (custom-shape normalization) and DirectionalGesturePath
// (chevron geometry) so the min/max scan lives in one place. Exposes the union
// of accessors both callers need; each uses the subset it requires.
final class GestureBounds {
    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;

    private GestureBounds(float minX, float maxX, float minY, float maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    static GestureBounds from(float[] xs, float[] ys) {
        float minX = xs[0];
        float maxX = xs[0];
        float minY = ys[0];
        float maxY = ys[0];
        for (int i = 1; i < xs.length; i++) {
            minX = Math.min(minX, xs[i]);
            maxX = Math.max(maxX, xs[i]);
            minY = Math.min(minY, ys[i]);
            maxY = Math.max(maxY, ys[i]);
        }
        return new GestureBounds(minX, maxX, minY, maxY);
    }

    float centerX() {
        return (minX + maxX) / 2f;
    }

    float centerY() {
        return (minY + maxY) / 2f;
    }

    float size() {
        return Math.max(maxX - minX, maxY - minY);
    }

    float width() {
        return maxX - minX;
    }

    float height() {
        return maxY - minY;
    }
}
