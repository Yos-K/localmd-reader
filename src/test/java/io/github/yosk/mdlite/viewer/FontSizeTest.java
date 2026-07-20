package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class FontSizeTest {

    @Test
    void acceptsMinimumValue() {
        FontSize fontSize = FontSize.of(FontSize.MIN_SP);

        TestAssertions.assertEquals(FontSize.MIN_SP, fontSize.sp(), "minimum font size must be accepted");
    }

    @Test
    void acceptsMaximumValue() {
        FontSize fontSize = FontSize.of(FontSize.MAX_SP);

        TestAssertions.assertEquals(FontSize.MAX_SP, fontSize.sp(), "maximum font size must be accepted");
    }

    @Test
    void rejectsValueBelowMinimum() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                FontSize.of(FontSize.MIN_SP - 1);
            }
        });
    }

    @Test
    void rejectsValueAboveMaximum() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                FontSize.of(FontSize.MAX_SP + 1);
            }
        });
    }

    @Test
    void increasesByOneStep() {
        FontSize fontSize = FontSize.of(16).increased();

        TestAssertions.assertEquals(17, fontSize.sp(), "increasing font size must add one sp");
    }

    @Test
    void doesNotIncreaseBeyondMaximum() {
        FontSize fontSize = FontSize.of(FontSize.MAX_SP).increased();

        TestAssertions.assertEquals(FontSize.MAX_SP, fontSize.sp(), "increasing maximum font size must stay at maximum");
    }

    @Test
    void decreasesByOneStep() {
        FontSize fontSize = FontSize.of(16).decreased();

        TestAssertions.assertEquals(15, fontSize.sp(), "decreasing font size must subtract one sp");
    }

    @Test
    void doesNotDecreaseBelowMinimum() {
        FontSize fontSize = FontSize.of(FontSize.MIN_SP).decreased();

        TestAssertions.assertEquals(FontSize.MIN_SP, fontSize.sp(), "decreasing minimum font size must stay at minimum");
    }

    @Test
    void pinchingOutScalesFromTheInitialFontSize() {
        FontSize fontSize = FontSize.of(16).changedByPinchScale(1.20f);

        TestAssertions.assertEquals(19, fontSize.sp(), "pinching out must keep the final size close to the visible pinch scale");
    }

    @Test
    void pinchingInScalesFromTheInitialFontSize() {
        FontSize fontSize = FontSize.of(16).changedByPinchScale(0.80f);

        TestAssertions.assertEquals(13, fontSize.sp(), "pinching in must keep the final size close to the visible pinch scale");
    }

    @Test
    void pinchingOutCannotExceedMaximumFontSize() {
        FontSize fontSize = FontSize.of(24).changedByPinchScale(2.00f);

        TestAssertions.assertEquals(FontSize.MAX_SP, fontSize.sp(), "pinching out must be clamped to the maximum font size");
    }

    @Test
    void pinchingInCannotGoBelowMinimumFontSize() {
        FontSize fontSize = FontSize.of(16).changedByPinchScale(0.20f);

        TestAssertions.assertEquals(FontSize.MIN_SP, fontSize.sp(), "pinching in must be clamped to the minimum font size");
    }

    @Test
    void pinchingWithTinyMovementKeepsCurrentSize() {
        FontSize fontSize = FontSize.of(16).changedByPinchScale(1.02f);

        TestAssertions.assertEquals(16, fontSize.sp(), "tiny pinch changes must not unexpectedly change the final font size");
    }

    @Test
    void pinchingWithNaNScaleKeepsCurrentSize() {
        FontSize fontSize = FontSize.of(18).changedByPinchScale(Float.NaN);

        TestAssertions.assertEquals(18, fontSize.sp(), "NaN pinch scale must be ignored to keep the current valid font size");
    }

    @Test
    void pinchingWithZeroScaleKeepsCurrentSize() {
        FontSize fontSize = FontSize.of(18).changedByPinchScale(0.0f);

        TestAssertions.assertEquals(18, fontSize.sp(), "zero pinch scale must be ignored to keep the current valid font size");
    }

    @Test
    void pinchingWithNegativeScaleKeepsCurrentSize() {
        FontSize fontSize = FontSize.of(18).changedByPinchScale(-1.0f);

        TestAssertions.assertEquals(18, fontSize.sp(), "negative pinch scale must be ignored to keep the current valid font size");
    }

    @Test
    void pinchingWithInfiniteScaleKeepsCurrentSize() {
        FontSize fontSize = FontSize.of(18).changedByPinchScale(Float.POSITIVE_INFINITY);

        TestAssertions.assertEquals(18, fontSize.sp(), "infinite pinch scale must be ignored to keep the current valid font size");
    }

    @Test
    void positiveFinitePinchScaleCanBeAppliedToAnOngoingGesture() {
        TestAssertions.assertTrue(FontSize.canApplyPinchScale(1.05f), "positive finite pinch scale must be accepted");
    }

    @Test
    void nanPinchScaleCannotBeAppliedToAnOngoingGesture() {
        TestAssertions.assertFalse(FontSize.canApplyPinchScale(Float.NaN), "NaN pinch scale must not corrupt the accumulated pinch scale");
    }

    @Test
    void zeroPinchScaleCannotBeAppliedToAnOngoingGesture() {
        TestAssertions.assertFalse(FontSize.canApplyPinchScale(0.0f), "zero pinch scale must not corrupt the accumulated pinch scale");
    }

    @Test
    void negativePinchScaleCannotBeAppliedToAnOngoingGesture() {
        TestAssertions.assertFalse(FontSize.canApplyPinchScale(-1.0f), "negative pinch scale must not corrupt the accumulated pinch scale");
    }

    @Test
    void infinitePinchScaleCannotBeAppliedToAnOngoingGesture() {
        TestAssertions.assertFalse(FontSize.canApplyPinchScale(Float.POSITIVE_INFINITY), "infinite pinch scale must not corrupt the accumulated pinch scale");
    }
}
