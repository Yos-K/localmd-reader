package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CustomGesturePreviewPathTest {
    private static final float WIDTH = 100f;
    private static final float HEIGHT = 100f;

    @Test
    void animationStartsAtTheRenderedCurveStart() {
        float[] point = CustomGesturePreviewPath.pointAt(WIDTH, HEIGHT, 0f);

        TestAssertions.assertEquals(2200, Math.round(point[0] * 100f),
                "animation x must start on the rendered custom path");
        TestAssertions.assertEquals(6200, Math.round(point[1] * 100f),
                "animation y must start on the rendered custom path");
    }

    @Test
    void animationMidpointUsesTheRenderedCubicCurveInsteadOfItsControlPolygon() {
        float[] point = CustomGesturePreviewPath.pointAt(WIDTH, HEIGHT, 0.4f);

        TestAssertions.assertEquals(4550, Math.round(point[0] * 100f),
                "the moving dot x must follow the cubic curve drawn behind it");
        TestAssertions.assertEquals(4950, Math.round(point[1] * 100f),
                "the moving dot y must follow the cubic curve drawn behind it");
    }

    @Test
    void animationReachesTheCubicEndpointBeforeFollowingTheFinalLine() {
        float[] point = CustomGesturePreviewPath.pointAt(WIDTH, HEIGHT, 0.8f);

        TestAssertions.assertEquals(7200, Math.round(point[0] * 100f),
                "the dot x must meet the rendered cubic endpoint at the segment boundary");
        TestAssertions.assertEquals(3400, Math.round(point[1] * 100f),
                "the dot y must meet the rendered cubic endpoint at the segment boundary");
    }

    @Test
    void animationEndsAtTheRenderedFinalLineEndpoint() {
        float[] point = CustomGesturePreviewPath.pointAt(WIDTH, HEIGHT, 1f);

        TestAssertions.assertEquals(8200, Math.round(point[0] * 100f),
                "animation x must end on the rendered custom path");
        TestAssertions.assertEquals(6800, Math.round(point[1] * 100f),
                "animation y must end on the rendered custom path");
    }
}
