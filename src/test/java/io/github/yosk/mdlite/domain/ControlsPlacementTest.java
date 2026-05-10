package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class ControlsPlacementTest {
    public static void main(String[] args) {
        ControlsPlacementTest test = new ControlsPlacementTest();
        test.topPlacementStoresTopValue();
        test.bottomPlacementStoresBottomValue();
        test.fromStoredValueRestoresBottomPlacement();
        test.fromStoredValueDefaultsUnknownValueToTopPlacement();
        test.toggledChangesTopToBottom();
        test.toggledChangesBottomToTop();
    }

    public void topPlacementStoresTopValue() {
        ControlsPlacement placement = ControlsPlacement.top();

        TestAssertions.assertEquals(ControlsPlacement.TOP_VALUE, placement.storedValue(), "top placement must store top value");
    }

    public void bottomPlacementStoresBottomValue() {
        ControlsPlacement placement = ControlsPlacement.bottom();

        TestAssertions.assertEquals(ControlsPlacement.BOTTOM_VALUE, placement.storedValue(), "bottom placement must store bottom value");
    }

    public void fromStoredValueRestoresBottomPlacement() {
        ControlsPlacement placement = ControlsPlacement.fromStoredValue(ControlsPlacement.BOTTOM_VALUE);

        TestAssertions.assertTrue(placement.isBottom(), "stored bottom value must restore bottom placement");
    }

    public void fromStoredValueDefaultsUnknownValueToTopPlacement() {
        ControlsPlacement placement = ControlsPlacement.fromStoredValue("unknown");

        TestAssertions.assertFalse(placement.isBottom(), "unknown stored value must default to top placement");
    }

    public void toggledChangesTopToBottom() {
        ControlsPlacement placement = ControlsPlacement.top().toggled();

        TestAssertions.assertTrue(placement.isBottom(), "toggling top placement must switch to bottom");
    }

    public void toggledChangesBottomToTop() {
        ControlsPlacement placement = ControlsPlacement.bottom().toggled();

        TestAssertions.assertFalse(placement.isBottom(), "toggling bottom placement must switch to top");
    }
}
