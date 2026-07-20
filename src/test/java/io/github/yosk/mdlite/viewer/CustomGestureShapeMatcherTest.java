package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CustomGestureShapeMatcherTest {

    @Test
    void matcherAcceptsSameShapeDrawnAtDifferentScale() {
        CustomGestureShapeMatcher matcher = CustomGestureShapeMatcher.forShape(zigzagShape());
        CustomGestureShape input = CustomGestureShape.fromPoints(
                new float[] { 40f, 100f, 160f, 220f },
                new float[] { 200f, 120f, 200f, 120f });

        TestAssertions.assertTrue(matcher.matches(input), "custom gesture matcher must accept the same shape at another scale and position");
    }

    @Test
    void matcherRejectsDifferentShape() {
        CustomGestureShapeMatcher matcher = CustomGestureShapeMatcher.forShape(zigzagShape());
        CustomGestureShape input = CustomGestureShape.fromPoints(
                new float[] { 40f, 100f, 160f, 220f },
                new float[] { 120f, 120f, 120f, 120f });

        TestAssertions.assertFalse(matcher.matches(input), "custom gesture matcher must reject a visibly different shape");
    }

    @Test
    void matcherDistinguishesDrawnCircleFromRegisteredClosedShape() {
        CustomGestureShapeMatcher matcher = CustomGestureShapeMatcher.forShape(squareShape());
        CustomGestureShape drawnCircle = CustomGestureShape.fromPoints(
                new float[] { 150f, 225f, 280f, 300f, 280f, 225f, 150f, 75f, 20f, 0f, 20f, 75f, 150f },
                new float[] { 0f, 20f, 75f, 150f, 225f, 280f, 300f, 280f, 225f, 150f, 75f, 20f, 0f });

        TestAssertions.assertFalse(matcher.matches(drawnCircle),
                "a drawn circle must not match a registered closed custom shape, so the circle binding and custom shortcuts stay distinguishable (GES5)");
    }

    @Test
    void matcherRejectsMissingInputShape() {
        CustomGestureShapeMatcher matcher = CustomGestureShapeMatcher.forShape(zigzagShape());

        TestAssertions.assertFalse(matcher.matches(null), "custom gesture matcher must reject missing input shapes");
    }

    @Test
    void matcherRequiresRegisteredShape() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                CustomGestureShapeMatcher.forShape(null);
            }
        });
    }

    private static CustomGestureShape zigzagShape() {
        return CustomGestureShape.fromPoints(
                new float[] { 10f, 60f, 110f, 160f },
                new float[] { 100f, 40f, 100f, 40f });
    }

    private static CustomGestureShape squareShape() {
        return CustomGestureShape.fromPoints(
                new float[] { 0f, 75f, 150f, 225f, 300f, 300f, 300f, 300f, 300f, 225f, 150f, 75f, 0f, 0f, 0f, 0f, 0f },
                new float[] { 0f, 0f, 0f, 0f, 0f, 75f, 150f, 225f, 300f, 300f, 300f, 300f, 300f, 225f, 150f, 75f, 0f });
    }
}
