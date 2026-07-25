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

    @Test
    void topRightPointInsideTheVisibleCancelLabelCancelsRegistration() {
        TestAssertions.assertTrue(
                CustomGestureDrawingLayout.isCancelTarget(968, 95, 180f, 900f, 150f),
                "a tap on the visible top-right cancel affordance must cancel registration");
    }

    @Test
    void drawingPointOutsideTheCancelLabelRemainsPartOfTheGesture() {
        TestAssertions.assertFalse(
                CustomGestureDrawingLayout.isCancelTarget(968, 95, 180f, 500f, 500f),
                "a point in the drawing surface must not be mistaken for cancellation");
    }

    @Test
    void visibleLeftSideOfTheCancelLabelBelongsToItsTouchTarget() {
        TestAssertions.assertTrue(
                CustomGestureDrawingLayout.isCancelTarget(968, 95, 180f, 740f, 150f),
                "the entire visible cancel label needs one matching touch target");
    }
}
