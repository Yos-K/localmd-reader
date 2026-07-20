package io.github.yosk.mdlite.viewer;

import java.util.Locale;

public final class CustomGestureShape {
    private static final int SAMPLE_COUNT = 32;
    // Units are dp (callers normalize px to dp first): rejects drawings too
    // physically small to compare reliably, on every screen density.
    private static final float MIN_SIZE_DP = 9f;

    private final float[] xs;
    private final float[] ys;

    private CustomGestureShape(float[] xs, float[] ys) {
        if (xs == null || ys == null || xs.length != SAMPLE_COUNT || ys.length != SAMPLE_COUNT) {
            throw new IllegalArgumentException("custom gesture shape must be normalized");
        }
        this.xs = copy(xs);
        this.ys = copy(ys);
    }

    public static CustomGestureShape fromPoints(float[] rawXs, float[] rawYs) {
        if (rawXs == null || rawYs == null || rawXs.length < 2 || rawXs.length != rawYs.length) {
            throw new IllegalArgumentException("custom gesture shape requires at least two matching point arrays");
        }
        GestureBounds bounds = GestureBounds.from(rawXs, rawYs);
        if (bounds.size() < MIN_SIZE_DP) {
            throw new IllegalArgumentException("custom gesture shape is too small");
        }
        GestureSampledPoints sampled = GestureSampledPoints.from(rawXs, rawYs, SAMPLE_COUNT);
        return new CustomGestureShape(normalizedXs(sampled.xs, bounds), normalizedYs(sampled.ys, bounds));
    }

    public static CustomGestureShape fromStoredValue(String storedValue) {
        if (storedValue == null || storedValue.length() == 0) {
            throw new IllegalArgumentException("stored custom gesture shape is required");
        }
        String[] pairs = storedValue.split(";", -1);
        if (pairs.length != SAMPLE_COUNT) {
            throw new IllegalArgumentException("stored custom gesture shape must contain normalized points");
        }
        float[] xs = new float[SAMPLE_COUNT];
        float[] ys = new float[SAMPLE_COUNT];
        for (int i = 0; i < pairs.length; i++) {
            String[] values = pairs[i].split(",", -1);
            if (values.length != 2) {
                throw new IllegalArgumentException("stored custom gesture point must contain x and y");
            }
            xs[i] = Float.parseFloat(values[0]);
            ys[i] = Float.parseFloat(values[1]);
        }
        return new CustomGestureShape(xs, ys);
    }

    public String storedValue() {
        StringBuilder stored = new StringBuilder();
        for (int i = 0; i < SAMPLE_COUNT; i++) {
            if (i > 0) {
                stored.append(';');
            }
            stored.append(String.format(Locale.US, "%.4f,%.4f", Float.valueOf(xs[i]), Float.valueOf(ys[i])));
        }
        return stored.toString();
    }

    public float averageDistanceTo(CustomGestureShape other) {
        if (other == null) {
            return Float.MAX_VALUE;
        }
        float total = 0f;
        for (int i = 0; i < SAMPLE_COUNT; i++) {
            total += distance(xs[i], ys[i], other.xs[i], other.ys[i]);
        }
        return total / SAMPLE_COUNT;
    }

    public int pointCount() {
        return SAMPLE_COUNT;
    }

    private static float[] normalizedXs(float[] rawXs, GestureBounds bounds) {
        float[] normalized = new float[rawXs.length];
        for (int i = 0; i < rawXs.length; i++) {
            normalized[i] = (rawXs[i] - bounds.centerX()) / bounds.size();
        }
        return normalized;
    }

    private static float[] normalizedYs(float[] rawYs, GestureBounds bounds) {
        float[] normalized = new float[rawYs.length];
        for (int i = 0; i < rawYs.length; i++) {
            normalized[i] = (rawYs[i] - bounds.centerY()) / bounds.size();
        }
        return normalized;
    }

    private static float[] copy(float[] source) {
        float[] result = new float[source.length];
        System.arraycopy(source, 0, result, 0, source.length);
        return result;
    }

    private static float distance(float startX, float startY, float endX, float endY) {
        float dx = endX - startX;
        float dy = endY - startY;
        return (float) Math.sqrt((dx * dx) + (dy * dy));
    }
}
