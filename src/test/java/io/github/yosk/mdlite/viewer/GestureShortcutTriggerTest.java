package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class GestureShortcutTriggerTest {

    @Test
    void storedDoubleTapValueRestoresDoubleTapTrigger() {
        GestureShortcutTrigger trigger = GestureShortcutTrigger.fromStoredValue("double_tap");

        TestAssertions.assertTrue(trigger.isDoubleTap(), "stored double_tap value must restore the double-tap trigger");
    }

    @Test
    void storedCircleValueRestoresCircleTrigger() {
        GestureShortcutTrigger trigger = GestureShortcutTrigger.fromStoredValue("circle");

        TestAssertions.assertTrue(trigger.isCircle(), "stored circle value must restore the circle trigger");
    }

    @Test
    void storedCustomShapeValueRestoresCustomShapeTrigger() {
        GestureShortcutTrigger trigger = GestureShortcutTrigger.fromStoredValue("custom_shape");

        TestAssertions.assertTrue(trigger.isCustomShape(), "stored custom_shape value must restore the custom shape trigger");
    }

    @Test
    void storedSwipeLeftValueRestoresSwipeLeftTrigger() {
        GestureShortcutTrigger trigger = GestureShortcutTrigger.fromStoredValue("swipe_left");

        TestAssertions.assertTrue(trigger.isSwipeLeft(), "stored swipe_left value must restore the swipe left trigger");
    }

    @Test
    void storedSwipeRightValueRestoresSwipeRightTrigger() {
        GestureShortcutTrigger trigger = GestureShortcutTrigger.fromStoredValue("swipe_right");

        TestAssertions.assertTrue(trigger.isSwipeRight(), "stored swipe_right value must restore the swipe right trigger");
    }

    @Test
    void storedSwipeUpValueRestoresSwipeUpTrigger() {
        GestureShortcutTrigger trigger = GestureShortcutTrigger.fromStoredValue("swipe_up");

        TestAssertions.assertTrue(trigger.isSwipeUp(), "stored swipe_up value must restore the swipe up trigger");
    }

    @Test
    void storedSwipeDownValueRestoresSwipeDownTrigger() {
        GestureShortcutTrigger trigger = GestureShortcutTrigger.fromStoredValue("swipe_down");

        TestAssertions.assertTrue(trigger.isSwipeDown(), "stored swipe_down value must restore the swipe down trigger");
    }

    @Test
    void unknownStoredValueRestoresDoubleTapTrigger() {
        GestureShortcutTrigger trigger = GestureShortcutTrigger.fromStoredValue("unknown");

        TestAssertions.assertTrue(trigger.isDoubleTap(), "unknown stored trigger values must restore a safe default trigger");
    }

    @Test
    void circleTriggerStoresStableValue() {
        GestureShortcutTrigger trigger = GestureShortcutTrigger.circle();

        TestAssertions.assertEquals("circle", trigger.storedValue(), "circle trigger must store a stable value");
    }
}
