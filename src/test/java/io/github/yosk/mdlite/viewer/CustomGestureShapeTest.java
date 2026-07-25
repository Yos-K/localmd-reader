package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class CustomGestureShapeTest {

    @Nested
    final class Normalization {
        @Test
        void pathIsNormalizedToStableSampleCount() {
            CustomGestureShape shape = CustomGestureShape.fromPoints(
                    new float[] { 10f, 40f, 80f, 120f },
                    new float[] { 20f, 50f, 40f, 90f });

            TestAssertions.assertEquals(32, shape.pointCount(),
                    "custom gesture shape must normalize paths to a stable sample count");
        }

        @Test
        void positionAndSizeDoNotChangeShapeIdentity() {
            CustomGestureShape original = diagonalShape();
            CustomGestureShape transformed = CustomGestureShape.fromPoints(
                    new float[] { 100f, 160f, 240f, 320f },
                    new float[] { 200f, 260f, 250f, 360f });

            TestAssertions.assertEquals(0, Math.round(original.averageDistanceTo(transformed) * 10f),
                    "custom gesture shape must ignore position and size");
        }

        @Test
        void resamplingDistributesPointsEvenlyAlongThePath() {
            // L-shaped path. Expected values derive from 32 equal arc-length samples
            // normalized around the center, rather than mirroring the implementation.
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
                    "resampling must space normalized stored points evenly along the path");
        }
    }

    @Nested
    final class Persistence {
        @Test
        void storedValueRestoresTheSameNormalizedShape() {
            CustomGestureShape original = diagonalShape();
            CustomGestureShape restored = CustomGestureShape.fromStoredValue(original.storedValue());

            TestAssertions.assertEquals(0, Math.round(original.averageDistanceTo(restored) * 1000f),
                    "stored custom gesture shape must restore the same normalized points");
        }

        @Test
        void nonFiniteStoredCoordinatesAreRejectedBeforeCreatingADomainObject() {
            final String corrupted = diagonalShape().storedValue().replaceFirst("-?[0-9]+\\.[0-9]+", "NaN");

            TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
                @Override
                public void run() {
                    CustomGestureShape.fromStoredValue(corrupted);
                }
            });
        }
    }

    @Nested
    final class ConstructionValidity {
        @Test
        void fewerThanTwoPointsAreRejected() {
            TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
                @Override
                public void run() {
                    CustomGestureShape.fromPoints(new float[] { 10f }, new float[] { 20f });
                }
            });
        }

        @Test
        void mismatchedCoordinateArraysAreRejected() {
            TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
                @Override
                public void run() {
                    CustomGestureShape.fromPoints(new float[] { 10f, 20f }, new float[] { 20f });
                }
            });
        }

        @Test
        void pathBelowMinimumPhysicalSizeIsRejected() {
            TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
                @Override
                public void run() {
                    CustomGestureShape.fromPoints(
                            new float[] { 10f, 12f, 14f }, new float[] { 20f, 21f, 22f });
                }
            });
        }

        @Test
        void pathAtMinimumPhysicalSizeIsAccepted() {
            CustomGestureShape atMinimum = CustomGestureShape.fromPoints(
                    new float[] { 0f, 9f }, new float[] { 0f, 0f });

            TestAssertions.assertTrue(atMinimum.storedValue().length() > 0,
                    "a drawing at exactly the 9dp minimum physical size must be registrable");
        }

        @Test
        void pathImmediatelyBelowMinimumPhysicalSizeIsRejected() {
            TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
                @Override
                public void run() {
                    CustomGestureShape.fromPoints(
                            new float[] { 0f, 8.9f }, new float[] { 0f, 0f });
                }
            });
        }

        @Test
        void nonFiniteDrawnCoordinatesAreRejectedBeforeNormalization() {
            TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
                @Override
                public void run() {
                    CustomGestureShape.fromPoints(
                            new float[] { 0f, Float.NaN, 100f },
                            new float[] { 0f, 50f, 100f });
                }
            });
        }
    }

    private static CustomGestureShape diagonalShape() {
        return CustomGestureShape.fromPoints(
                new float[] { 10f, 40f, 80f, 120f },
                new float[] { 20f, 50f, 45f, 95f });
    }
}
