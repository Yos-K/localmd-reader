package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ControlsPlacementTest {

    @Test
    void topPlacementStoresTopValue() {
        ControlsPlacement placement = ControlsPlacement.top();

        TestAssertions.assertEquals(ControlsPlacement.TOP_VALUE, placement.storedValue(), "top placement must store top value");
    }

    @Test
    void bottomPlacementStoresBottomValue() {
        ControlsPlacement placement = ControlsPlacement.bottom();

        TestAssertions.assertEquals(ControlsPlacement.BOTTOM_VALUE, placement.storedValue(), "bottom placement must store bottom value");
    }

    @Test
    void fromStoredValueRestoresBottomPlacement() {
        ControlsPlacement placement = ControlsPlacement.fromStoredValue(ControlsPlacement.BOTTOM_VALUE);

        TestAssertions.assertTrue(placement.isBottom(), "stored bottom value must restore bottom placement");
    }

    @Test
    void fromStoredValueDefaultsUnknownValueToTopPlacement() {
        ControlsPlacement placement = ControlsPlacement.fromStoredValue("unknown");

        TestAssertions.assertFalse(placement.isBottom(), "unknown stored value must default to top placement");
    }

    @Test
    void toggledChangesTopToBottom() {
        ControlsPlacement placement = ControlsPlacement.top().toggled();

        TestAssertions.assertTrue(placement.isBottom(), "toggling top placement must switch to bottom");
    }

    @Test
    void toggledChangesBottomToTop() {
        ControlsPlacement placement = ControlsPlacement.bottom().toggled();

        TestAssertions.assertFalse(placement.isBottom(), "toggling bottom placement must switch to top");
    }
}
