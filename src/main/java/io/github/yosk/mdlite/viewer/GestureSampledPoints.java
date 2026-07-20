package io.github.yosk.mdlite.viewer;

// Resamples a raw point path to a fixed number of points spaced evenly by arc
// length, so two gestures drawn at different speeds/segment counts compare on
// the same footing. Shared by CustomGestureShape and DirectionalGesturePath.
//
// Degenerate (zero total travel) input is returned as a copy rather than
// resampled — there is no arc length to distribute along. DirectionalGesturePath
// relies on this; CustomGestureShape never reaches it because its caller rejects
// drawings below MIN_SIZE_DP first (a >=9dp span implies non-zero travel).
final class GestureSampledPoints {
    final float[] xs;
    final float[] ys;

    private GestureSampledPoints(float[] xs, float[] ys) {
        this.xs = xs;
        this.ys = ys;
    }

    static GestureSampledPoints from(float[] rawXs, float[] rawYs, int sampleCount) {
        float totalDistance = totalDistance(rawXs, rawYs);
        if (totalDistance == 0f) {
            return new GestureSampledPoints(copy(rawXs), copy(rawYs));
        }
        float[] sampledXs = new float[sampleCount];
        float[] sampledYs = new float[sampleCount];
        sampledXs[0] = rawXs[0];
        sampledYs[0] = rawYs[0];
        sampledXs[sampleCount - 1] = rawXs[rawXs.length - 1];
        sampledYs[sampleCount - 1] = rawYs[rawYs.length - 1];
        int segmentIndex = 1;
        float traveledBeforeSegment = 0f;
        for (int sampleIndex = 1; sampleIndex < sampleCount - 1; sampleIndex++) {
            float targetDistance = totalDistance * sampleIndex / (sampleCount - 1f);
            float segmentDistance = segmentDistance(rawXs, rawYs, segmentIndex);
            while (segmentIndex < rawXs.length - 1 && traveledBeforeSegment + segmentDistance < targetDistance) {
                traveledBeforeSegment += segmentDistance;
                segmentIndex++;
                segmentDistance = segmentDistance(rawXs, rawYs, segmentIndex);
            }
            float ratio = (targetDistance - traveledBeforeSegment) / Math.max(1f, segmentDistance);
            sampledXs[sampleIndex] = rawXs[segmentIndex - 1] + ((rawXs[segmentIndex] - rawXs[segmentIndex - 1]) * ratio);
            sampledYs[sampleIndex] = rawYs[segmentIndex - 1] + ((rawYs[segmentIndex] - rawYs[segmentIndex - 1]) * ratio);
        }
        return new GestureSampledPoints(sampledXs, sampledYs);
    }

    private static float totalDistance(float[] xs, float[] ys) {
        float total = 0f;
        for (int i = 1; i < xs.length; i++) {
            total += distance(xs[i - 1], ys[i - 1], xs[i], ys[i]);
        }
        return total;
    }

    private static float segmentDistance(float[] xs, float[] ys, int segmentIndex) {
        return distance(xs[segmentIndex - 1], ys[segmentIndex - 1], xs[segmentIndex], ys[segmentIndex]);
    }

    private static float distance(float startX, float startY, float endX, float endY) {
        float dx = endX - startX;
        float dy = endY - startY;
        return (float) Math.sqrt((dx * dx) + (dy * dy));
    }

    private static float[] copy(float[] source) {
        float[] result = new float[source.length];
        System.arraycopy(source, 0, result, 0, source.length);
        return result;
    }
}
