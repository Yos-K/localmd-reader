package io.github.yosk.mdlite.viewer;

public final class CustomGesturePreviewPath {
    private static final float CURVE_PROGRESS_END = 0.8f;

    private CustomGesturePreviewPath() {
    }

    public static float[][] points(float width, float height) {
        return new float[][] {
            { width * 0.22f, height * 0.62f },
            { width * 0.36f, height * 0.18f },
            { width * 0.54f, height * 0.82f },
            { width * 0.72f, height * 0.34f },
            { width * 0.82f, height * 0.68f }
        };
    }

    public static float[] pointAt(float width, float height, float progress) {
        float bounded = boundedProgress(progress);
        float[][] path = points(width, height);
        if (bounded <= CURVE_PROGRESS_END) {
            return cubicPoint(path, bounded / CURVE_PROGRESS_END);
        }
        return linePoint(path[3], path[4],
                (bounded - CURVE_PROGRESS_END) / (1f - CURVE_PROGRESS_END));
    }

    private static float[] cubicPoint(float[][] points, float progress) {
        float inverse = 1f - progress;
        float startWeight = inverse * inverse * inverse;
        float firstControlWeight = 3f * inverse * inverse * progress;
        float secondControlWeight = 3f * inverse * progress * progress;
        float endWeight = progress * progress * progress;
        return new float[] {
            (points[0][0] * startWeight) + (points[1][0] * firstControlWeight)
                    + (points[2][0] * secondControlWeight) + (points[3][0] * endWeight),
            (points[0][1] * startWeight) + (points[1][1] * firstControlWeight)
                    + (points[2][1] * secondControlWeight) + (points[3][1] * endWeight)
        };
    }

    private static float[] linePoint(float[] start, float[] end, float progress) {
        return new float[] {
            start[0] + ((end[0] - start[0]) * progress),
            start[1] + ((end[1] - start[1]) * progress)
        };
    }

    private static float boundedProgress(float progress) {
        if (Float.isNaN(progress) || progress <= 0f) {
            return 0f;
        }
        return Math.min(progress, 1f);
    }
}
