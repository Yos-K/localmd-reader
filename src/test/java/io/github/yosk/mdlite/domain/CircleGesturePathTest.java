package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class CircleGesturePathTest {
    public static void main(String[] args) {
        closedRoundPathIsRecognizedAsCircleGesture();
        openPathIsNotRecognizedAsCircleGesture();
        tinyClosedPathIsNotRecognizedAsCircleGesture();
        flatClosedPathIsNotRecognizedAsCircleGesture();
    }

    private static void closedRoundPathIsRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 100f, 140f, 170f, 140f, 100f, 60f, 30f, 60f, 100f },
                new float[] { 40f, 55f, 100f, 145f, 160f, 145f, 100f, 55f, 40f });

        TestAssertions.assertTrue(path.isCircleLike(), "closed round one-finger path must be recognized as a circle gesture");
    }

    private static void openPathIsNotRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 20f, 60f, 100f, 140f, 180f },
                new float[] { 20f, 60f, 100f, 140f, 180f });

        TestAssertions.assertFalse(path.isCircleLike(), "open one-finger path must not be recognized as a circle gesture");
    }

    private static void tinyClosedPathIsNotRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 10f, 14f, 18f, 14f, 10f, 6f, 2f, 6f, 10f },
                new float[] { 4f, 6f, 10f, 14f, 16f, 14f, 10f, 6f, 4f });

        TestAssertions.assertFalse(path.isCircleLike(), "tiny closed path must not trigger a circle shortcut");
    }

    private static void flatClosedPathIsNotRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 20f, 80f, 140f, 200f, 140f, 80f, 20f },
                new float[] { 50f, 52f, 54f, 50f, 46f, 48f, 50f });

        TestAssertions.assertFalse(path.isCircleLike(), "flat closed path must not be recognized as a circle gesture");
    }
}
