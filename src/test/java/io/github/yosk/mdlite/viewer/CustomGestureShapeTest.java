package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CustomGestureShapeTest {

    @Test
    void shapeNormalizesAPathToStableSampleCount() {
        CustomGestureShape shape = CustomGestureShape.fromPoints(
                new float[] { 10f, 40f, 80f, 120f },
                new float[] { 20f, 50f, 40f, 90f });

        TestAssertions.assertEquals(32, shape.pointCount(), "custom gesture shape must normalize paths to a stable sample count");
    }

    @Test
    void shapeCanRoundTripThroughStoredValue() {
        CustomGestureShape original = diagonalShape();
        CustomGestureShape restored = CustomGestureShape.fromStoredValue(original.storedValue());

        TestAssertions.assertEquals(0, Math.round(original.averageDistanceTo(restored) * 1000f), "stored custom gesture shape must restore the same normalized points");
    }

    @Test
    void shapeRejectsTooFewPoints() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                CustomGestureShape.fromPoints(new float[] { 10f }, new float[] { 20f });
            }
        });
    }

    @Test
    void shapeRejectsMismatchedPointArrays() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                CustomGestureShape.fromPoints(new float[] { 10f, 20f }, new float[] { 20f });
            }
        });
    }

    @Test
    void shapeRejectsTinyPath() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                CustomGestureShape.fromPoints(new float[] { 10f, 12f, 14f }, new float[] { 20f, 21f, 22f });
            }
        });
    }

    @Test
    void sameShapeAtDifferentPositionAndSizeHasSmallDistance() {
        CustomGestureShape original = diagonalShape();
        CustomGestureShape transformed = CustomGestureShape.fromPoints(
                new float[] { 100f, 160f, 240f, 320f },
                new float[] { 200f, 260f, 250f, 360f });

        TestAssertions.assertEquals(0, Math.round(original.averageDistanceTo(transformed) * 10f), "custom gesture shape must ignore position and size");
    }

    @Test
    void resamplingDistributesPointsEvenlyAlongThePath() {
        // L字パス (0,0)→(0,300)→(300,300)。期待値は仕様（弧長等間隔32点を
        // 中心原点 [-0.5,0.5] に正規化）から導出した値で、実装の写しではない。
        CustomGestureShape shape = CustomGestureShape.fromPoints(
                new float[] { 0f, 0f, 300f },
                new float[] { 0f, 300f, 300f });

        TestAssertions.assertEquals(
                "-0.5000,-0.5000;-0.5000,-0.4355;-0.5000,-0.3710;-0.5000,-0.3065;-0.5000,-0.2419;"
                        + "-0.5000,-0.1774;-0.5000,-0.1129;-0.5000,-0.0484;-0.5000,0.0161;-0.5000,0.0806;"
                        + "-0.5000,0.1452;-0.5000,0.2097;-0.5000,0.2742;-0.5000,0.3387;-0.5000,0.4032;"
                        + "-0.5000,0.4677;-0.4677,0.5000;-0.4032,0.5000;-0.3387,0.5000;-0.2742,0.5000;"
                        + "-0.2097,0.5000;-0.1452,0.5000;-0.0806,0.5000;-0.0161,0.5000;0.0484,0.5000;"
                        + "0.1129,0.5000;0.1774,0.5000;0.2419,0.5000;0.3065,0.5000;0.3710,0.5000;"
                        + "0.4355,0.5000;0.5000,0.5000",
                shape.storedValue(),
                "resampling must space the 32 stored points evenly along the drawn path (arc length), normalized around the center");
    }

    @Test
    void shapeAtMinimumSizeIsAcceptedAndSmallerIsRejected() {
        CustomGestureShape atMinimum = CustomGestureShape.fromPoints(
                new float[] { 0f, 9f }, new float[] { 0f, 0f });

        TestAssertions.assertTrue(atMinimum.storedValue().length() > 0,
                "a drawing at exactly the 9dp minimum physical size must be registrable");
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                CustomGestureShape.fromPoints(new float[] { 0f, 8.9f }, new float[] { 0f, 0f });
            }
        });
    }

    private static CustomGestureShape diagonalShape() {
        return CustomGestureShape.fromPoints(
                new float[] { 10f, 40f, 80f, 120f },
                new float[] { 20f, 50f, 45f, 95f });
    }
}
