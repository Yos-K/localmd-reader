package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CustomGestureDrawingLayoutTest {
    @Test
    void instructionBaselineStartsBelowTheStatusBarInset() {
        TestAssertions.assertEquals(167, CustomGestureDrawingLayout.instructionBaseline(95),
                "the drawing instruction must remain below a 95px status bar inset");
    }

    @Test
    void instructionBaselineKeepsItsTopSpacingWithoutAStatusBarInset() {
        TestAssertions.assertEquals(72, CustomGestureDrawingLayout.instructionBaseline(0),
                "the drawing instruction must retain its normal top spacing without an inset");
    }
}
