package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CircleGesturePathTest {

    @Test
    void closedRoundPathIsRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 100f, 140f, 170f, 140f, 100f, 60f, 30f, 60f, 100f },
                new float[] { 40f, 55f, 100f, 145f, 160f, 145f, 100f, 55f, 40f });

        TestAssertions.assertTrue(path.isCircleLike(), "closed round one-finger path must be recognized as a circle gesture");
    }

    @Test
    void openPathIsNotRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 20f, 60f, 100f, 140f, 180f },
                new float[] { 20f, 60f, 100f, 140f, 180f });

        TestAssertions.assertFalse(path.isCircleLike(), "open one-finger path must not be recognized as a circle gesture");
    }

    @Test
    void tinyClosedPathIsNotRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 10f, 14f, 18f, 14f, 10f, 6f, 2f, 6f, 10f },
                new float[] { 4f, 6f, 10f, 14f, 16f, 14f, 10f, 6f, 4f });

        TestAssertions.assertFalse(path.isCircleLike(), "tiny closed path must not trigger a circle shortcut");
    }

    @Test
    void flatClosedPathIsNotRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 20f, 80f, 140f, 200f, 140f, 80f, 20f },
                new float[] { 50f, 52f, 54f, 50f, 46f, 48f, 50f });

        TestAssertions.assertFalse(path.isCircleLike(), "flat closed path must not be recognized as a circle gesture");
    }

    @Test
    void imperfectClosedPathIsRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 100f, 150f, 178f, 151f, 105f, 57f, 35f, 65f, 121f },
                new float[] { 42f, 60f, 104f, 148f, 166f, 141f, 95f, 54f, 51f });

        TestAssertions.assertTrue(path.isCircleLike(), "imperfect one-finger circle path must still trigger the circle shortcut");
    }

    @Test
    void nearlyClosedFastReleasePathIsRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 100f, 150f, 178f, 156f, 104f, 55f, 28f, 48f, 62f },
                new float[] { 42f, 58f, 101f, 149f, 164f, 144f, 99f, 58f, 62f });

        TestAssertions.assertTrue(path.isCircleLike(), "nearly closed circle path must trigger when the finger is released quickly");
    }

    @Test
    void closedSquarePathIsRecognizedAsCircleGesture() {
        CircleGesturePath path = CircleGesturePath.fromPoints(
                new float[] { 0f, 100f, 200f, 200f, 200f, 100f, 0f, 0f, 0f },
                new float[] { 0f, 0f, 0f, 100f, 200f, 200f, 200f, 100f, 0f });

        TestAssertions.assertTrue(path.isCircleLike(),
                "circle detection is deliberately lenient about roundness: any closed non-chevron path counts as a circle (GES1, owner-approved intent)");
    }
}
