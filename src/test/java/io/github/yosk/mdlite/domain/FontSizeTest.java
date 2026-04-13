package io.github.yosk.mdlite.domain;

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

        assertEquals(FontSize.MIN_SP, fontSize.sp(), "minimum font size must be accepted");
    }

    public void acceptsMaximumValue() {
        FontSize fontSize = FontSize.of(FontSize.MAX_SP);

        assertEquals(FontSize.MAX_SP, fontSize.sp(), "maximum font size must be accepted");
    }

    public void rejectsValueBelowMinimum() {
        assertThrows(new Runnable() {
            @Override
            public void run() {
                FontSize.of(FontSize.MIN_SP - 1);
            }
        }, "font size below minimum must be rejected");
    }

    public void rejectsValueAboveMaximum() {
        assertThrows(new Runnable() {
            @Override
            public void run() {
                FontSize.of(FontSize.MAX_SP + 1);
            }
        }, "font size above maximum must be rejected");
    }

    public void increasesByOneStep() {
        FontSize fontSize = FontSize.of(16).increased();

        assertEquals(17, fontSize.sp(), "increasing font size must add one sp");
    }

    public void doesNotIncreaseBeyondMaximum() {
        FontSize fontSize = FontSize.of(FontSize.MAX_SP).increased();

        assertEquals(FontSize.MAX_SP, fontSize.sp(), "increasing maximum font size must stay at maximum");
    }

    public void decreasesByOneStep() {
        FontSize fontSize = FontSize.of(16).decreased();

        assertEquals(15, fontSize.sp(), "decreasing font size must subtract one sp");
    }

    public void doesNotDecreaseBelowMinimum() {
        FontSize fontSize = FontSize.of(FontSize.MIN_SP).decreased();

        assertEquals(FontSize.MIN_SP, fontSize.sp(), "decreasing minimum font size must stay at minimum");
    }

    public void pinchingOutBeyondThresholdIncreasesByOneStep() {
        FontSize fontSize = FontSize.of(16).changedByPinchScale(1.12f);

        assertEquals(17, fontSize.sp(), "pinching out beyond the threshold must increase font size by one step");
    }

    public void pinchingInBeyondThresholdDecreasesByOneStep() {
        FontSize fontSize = FontSize.of(16).changedByPinchScale(0.88f);

        assertEquals(15, fontSize.sp(), "pinching in beyond the threshold must decrease font size by one step");
    }

    public void pinchingWithinThresholdKeepsCurrentSize() {
        FontSize fontSize = FontSize.of(16).changedByPinchScale(1.04f);

        assertEquals(16, fontSize.sp(), "small pinch changes must not change font size");
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertThrows(Runnable runnable, String message) {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError(message);
    }
}
