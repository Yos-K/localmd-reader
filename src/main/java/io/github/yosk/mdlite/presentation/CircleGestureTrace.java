package io.github.yosk.mdlite.presentation;

import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.List;
import io.github.yosk.mdlite.viewer.CircleGesturePath;
import io.github.yosk.mdlite.viewer.CustomGestureShape;
import io.github.yosk.mdlite.viewer.DirectionalGesturePath;
import io.github.yosk.mdlite.viewer.GestureShortcutTrigger;

final class CircleGestureTrace {
    private final List<Float> xs = new ArrayList<Float>();
    private final List<Float> ys = new ArrayList<Float>();
    private final float density;
    private boolean consuming;

    CircleGestureTrace(float density) {
        if (density <= 0f || Float.isNaN(density)) {
            throw new IllegalArgumentException("display density must be positive");
        }
        this.density = density;
    }

    void append(MotionEvent event) {
        // Store dp, not px: recognition thresholds downstream are physical
        // sizes, so the same finger movement matches on every density (#147).
        xs.add(Float.valueOf(event.getX() / density));
        ys.add(Float.valueOf(event.getY() / density));
    }

    void reset() {
        xs.clear();
        ys.clear();
        consuming = false;
    }

    boolean rememberIntentionalMovement(float minimumSizeDp) {
        consuming = consuming || looksIntentional(minimumSizeDp);
        return consuming;
    }

    boolean isConsuming() {
        return consuming;
    }

    boolean isCircleLike() {
        return CircleGesturePath.fromPoints(toFloatArray(xs), toFloatArray(ys)).isCircleLike();
    }

    CustomGestureShape customGestureShape() {
        return CustomGestureShape.fromPoints(toFloatArray(xs), toFloatArray(ys));
    }

    GestureShortcutTrigger directionalGestureTrigger() {
        return DirectionalGesturePath.fromPoints(toFloatArray(xs), toFloatArray(ys)).trigger();
    }

    private boolean looksIntentional(float minimumSizeDp) {
        if (xs.size() < 5) {
            return false;
        }
        Bounds bounds = Bounds.from(xs, ys);
        if (bounds.width() < minimumSizeDp || bounds.height() < minimumSizeDp) {
            return false;
        }
        return bounds.aspectRatio() <= 1.8f;
    }

    private static float[] toFloatArray(List<Float> values) {
        float[] result = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i).floatValue();
        }
        return result;
    }

    private static final class Bounds {
        private final float minX;
        private final float maxX;
        private final float minY;
        private final float maxY;

        private Bounds(float minX, float maxX, float minY, float maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        static Bounds from(List<Float> xs, List<Float> ys) {
            float minX = xs.get(0).floatValue();
            float maxX = minX;
            float minY = ys.get(0).floatValue();
            float maxY = minY;
            for (int i = 1; i < xs.size(); i++) {
                minX = Math.min(minX, xs.get(i).floatValue());
                maxX = Math.max(maxX, xs.get(i).floatValue());
                minY = Math.min(minY, ys.get(i).floatValue());
                maxY = Math.max(maxY, ys.get(i).floatValue());
            }
            return new Bounds(minX, maxX, minY, maxY);
        }

        float width() {
            return maxX - minX;
        }

        float height() {
            return maxY - minY;
        }

        float aspectRatio() {
            return Math.max(width(), height()) / Math.max(1f, Math.min(width(), height()));
        }
    }
}
