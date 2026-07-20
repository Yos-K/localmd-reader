package io.github.yosk.mdlite.viewer;

public final class DirectionalGesturePath {
    private static final int SAMPLE_COUNT = 32;
    // Units are dp: callers (CircleGestureTrace) normalize px to dp before
    // building paths, so this threshold is the same physical size everywhere.
    private static final float MIN_SIZE_DP = 28f;
    private static final float MIN_APEX_DEPTH_RATIO = 0.55f;
    private static final float MAX_BASE_DRIFT_RATIO = 0.45f;
    private static final float MIN_LEG_SPAN_RATIO = 0.28f;

    private final float[] xs;
    private final float[] ys;

    private DirectionalGesturePath(float[] xs, float[] ys) {
        this.xs = xs;
        this.ys = ys;
    }

    public static DirectionalGesturePath fromPoints(float[] xs, float[] ys) {
        if (xs == null || ys == null || xs.length < 3 || xs.length != ys.length) {
            return new DirectionalGesturePath(new float[0], new float[0]);
        }
        GestureSampledPoints sampled = GestureSampledPoints.from(xs, ys, SAMPLE_COUNT);
        return new DirectionalGesturePath(sampled.xs, sampled.ys);
    }

    public GestureShortcutTrigger trigger() {
        if (xs.length < 3) {
            return null;
        }
        GestureBounds bounds = GestureBounds.from(xs, ys);
        if (bounds.size() < MIN_SIZE_DP) {
            return null;
        }
        GestureShortcutTrigger horizontal = horizontalChevron(bounds);
        if (horizontal != null) {
            return horizontal;
        }
        return verticalChevron(bounds);
    }

    private GestureShortcutTrigger horizontalChevron(GestureBounds bounds) {
        if (isLeftChevron(bounds, minXIndex())) {
            return GestureShortcutTrigger.swipeLeft();
        }
        if (isRightChevron(bounds, maxXIndex())) {
            return GestureShortcutTrigger.swipeRight();
        }
        return null;
    }

    private GestureShortcutTrigger verticalChevron(GestureBounds bounds) {
        if (isUpChevron(bounds, minYIndex())) {
            return GestureShortcutTrigger.swipeUp();
        }
        if (isDownChevron(bounds, maxYIndex())) {
            return GestureShortcutTrigger.swipeDown();
        }
        return null;
    }

    private boolean isLeftChevron(GestureBounds bounds, int apexIndex) {
        if (!apexIsInside(apexIndex)) {
            return false;
        }
        float width = bounds.width();
        float height = bounds.height();
        float startX = xs[0];
        float endX = xs[xs.length - 1];
        float apexX = xs[apexIndex];
        float startY = ys[0];
        float endY = ys[ys.length - 1];
        float apexY = ys[apexIndex];
        return hasVerticalLegs(startY, endY, apexY, height)
                && Math.abs(startX - endX) <= width * MAX_BASE_DRIFT_RATIO
                && Math.min(startX, endX) - apexX >= width * MIN_APEX_DEPTH_RATIO;
    }

    private boolean isRightChevron(GestureBounds bounds, int apexIndex) {
        if (!apexIsInside(apexIndex)) {
            return false;
        }
        float width = bounds.width();
        float height = bounds.height();
        float startX = xs[0];
        float endX = xs[xs.length - 1];
        float apexX = xs[apexIndex];
        float startY = ys[0];
        float endY = ys[ys.length - 1];
        float apexY = ys[apexIndex];
        return hasVerticalLegs(startY, endY, apexY, height)
                && Math.abs(startX - endX) <= width * MAX_BASE_DRIFT_RATIO
                && apexX - Math.max(startX, endX) >= width * MIN_APEX_DEPTH_RATIO;
    }

    private boolean isUpChevron(GestureBounds bounds, int apexIndex) {
        if (!apexIsInside(apexIndex)) {
            return false;
        }
        float width = bounds.width();
        float height = bounds.height();
        float startX = xs[0];
        float endX = xs[xs.length - 1];
        float apexX = xs[apexIndex];
        float startY = ys[0];
        float endY = ys[ys.length - 1];
        float apexY = ys[apexIndex];
        return hasHorizontalLegs(startX, endX, apexX, width)
                && Math.abs(startY - endY) <= height * MAX_BASE_DRIFT_RATIO
                && Math.min(startY, endY) - apexY >= height * MIN_APEX_DEPTH_RATIO;
    }

    private boolean isDownChevron(GestureBounds bounds, int apexIndex) {
        if (!apexIsInside(apexIndex)) {
            return false;
        }
        float width = bounds.width();
        float height = bounds.height();
        float startX = xs[0];
        float endX = xs[xs.length - 1];
        float apexX = xs[apexIndex];
        float startY = ys[0];
        float endY = ys[ys.length - 1];
        float apexY = ys[apexIndex];
        return hasHorizontalLegs(startX, endX, apexX, width)
                && Math.abs(startY - endY) <= height * MAX_BASE_DRIFT_RATIO
                && apexY - Math.max(startY, endY) >= height * MIN_APEX_DEPTH_RATIO;
    }

    private static boolean hasVerticalLegs(float startY, float endY, float apexY, float height) {
        return Math.abs(startY - apexY) >= height * MIN_LEG_SPAN_RATIO
                && Math.abs(endY - apexY) >= height * MIN_LEG_SPAN_RATIO
                && Math.signum(startY - apexY) != Math.signum(endY - apexY);
    }

    private static boolean hasHorizontalLegs(float startX, float endX, float apexX, float width) {
        return Math.abs(startX - apexX) >= width * MIN_LEG_SPAN_RATIO
                && Math.abs(endX - apexX) >= width * MIN_LEG_SPAN_RATIO
                && Math.signum(startX - apexX) != Math.signum(endX - apexX);
    }

    private boolean apexIsInside(int index) {
        return index > 0 && index < xs.length - 1;
    }

    private int minXIndex() {
        int index = 0;
        for (int i = 1; i < xs.length; i++) {
            if (xs[i] < xs[index]) {
                index = i;
            }
        }
        return index;
    }

    private int maxXIndex() {
        int index = 0;
        for (int i = 1; i < xs.length; i++) {
            if (xs[i] > xs[index]) {
                index = i;
            }
        }
        return index;
    }

    private int minYIndex() {
        int index = 0;
        for (int i = 1; i < ys.length; i++) {
            if (ys[i] < ys[index]) {
                index = i;
            }
        }
        return index;
    }

    private int maxYIndex() {
        int index = 0;
        for (int i = 1; i < ys.length; i++) {
            if (ys[i] > ys[index]) {
                index = i;
            }
        }
        return index;
    }

}
