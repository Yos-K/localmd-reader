package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class FontSizeTest {
    public static void main(String[] args) {
        FontSizeTest test = new FontSizeTest();
        test.acceptsMinimumValue();
        test.acceptsMaximumValue();
        test.rejectsValueBelowMinimum();
        test.rejectsValueAboveMaximum();
        test.increasesByOneStep();
        test.doesNotIncreaseBeyondMaximum();
        test.decreasesByOneStep();
        test.doesNotDecreaseBelowMinimum();
        test.pinchingOutBeyondThresholdIncreasesByOneStep();
        test.pinchingInBeyondThresholdDecreasesByOneStep();
        test.pinchingWithinThresholdKeepsCurrentSize();
    }

    public void acceptsMinimumValue() {
        FontSize fontSize = FontSize.of(FontSize.MIN_SP);

        TestAssertions.assertEquals(FontSize.MIN_SP, fontSize.sp(), "minimum font size must be accepted");
    }

    public void acceptsMaximumValue() {
        FontSize fontSize = FontSize.of(FontSize.MAX_SP);

        TestAssertions.assertEquals(FontSize.MAX_SP, fontSize.sp(), "maximum font size must be accepted");
    }

    public void rejectsValueBelowMinimum() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                FontSize.of(FontSize.MIN_SP - 1);
            }
        });
    }

    public void rejectsValueAboveMaximum() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                FontSize.of(FontSize.MAX_SP + 1);
            }
        });
    }

    public void increasesByOneStep() {
        FontSize fontSize = FontSize.of(16).increased();

        TestAssertions.assertEquals(17, fontSize.sp(), "increasing font size must add one sp");
    }

    public void doesNotIncreaseBeyondMaximum() {
        FontSize fontSize = FontSize.of(FontSize.MAX_SP).increased();

        TestAssertions.assertEquals(FontSize.MAX_SP, fontSize.sp(), "increasing maximum font size must stay at maximum");
    }

    public void decreasesByOneStep() {
        FontSize fontSize = FontSize.of(16).decreased();

        TestAssertions.assertEquals(15, fontSize.sp(), "decreasing font size must subtract one sp");
    }

    public void doesNotDecreaseBelowMinimum() {
        FontSize fontSize = FontSize.of(FontSize.MIN_SP).decreased();

        TestAssertions.assertEquals(FontSize.MIN_SP, fontSize.sp(), "decreasing minimum font size must stay at minimum");
    }

    public void pinchingOutBeyondThresholdIncreasesByOneStep() {
        FontSize fontSize = FontSize.of(16).changedByPinchScale(1.12f);

        TestAssertions.assertEquals(17, fontSize.sp(), "pinching out beyond the threshold must increase font size by one step");
    }

    public void pinchingInBeyondThresholdDecreasesByOneStep() {
        FontSize fontSize = FontSize.of(16).changedByPinchScale(0.88f);

        TestAssertions.assertEquals(15, fontSize.sp(), "pinching in beyond the threshold must decrease font size by one step");
    }

    public void pinchingWithinThresholdKeepsCurrentSize() {
        FontSize fontSize = FontSize.of(16).changedByPinchScale(1.04f);

        TestAssertions.assertEquals(16, fontSize.sp(), "small pinch changes must not change font size");
    }
}
