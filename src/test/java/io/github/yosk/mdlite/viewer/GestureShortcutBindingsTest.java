package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class GestureShortcutBindingsTest {

    @Test
    void emptyBindingsReturnOffForUnknownTrigger() {
        GestureShortcutAction action = GestureShortcutBindings.empty()
                .actionFor(GestureShortcutTrigger.circle());

        TestAssertions.assertTrue(action.isOff(), "empty bindings must return Off for any trigger");
    }

    @Test
    void putBindingMakesActionAvailableByTrigger() {
        GestureShortcutAction action = GestureShortcutBindings.empty()
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.circle(), GestureShortcutAction.openMenu()))
                .actionFor(GestureShortcutTrigger.circle());

        TestAssertions.assertTrue(action.isOpenMenu(), "put binding must make its action available by trigger");
    }

    @Test
    void puttingSameTriggerReplacesPreviousAction() {
        GestureShortcutAction action = GestureShortcutBindings.empty()
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.circle(), GestureShortcutAction.openMenu()))
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.circle(), GestureShortcutAction.nextTab()))
                .actionFor(GestureShortcutTrigger.circle());

        TestAssertions.assertTrue(action.isNextTab(), "putting the same trigger must replace the previous action");
    }

    @Test
    void puttingDifferentTriggersKeepsBothBindings() {
        GestureShortcutBindings bindings = GestureShortcutBindings.empty()
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.doubleTap(), GestureShortcutAction.openMenu()))
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.circle(), GestureShortcutAction.nextTab()));

        TestAssertions.assertEquals(2, bindings.items().size(), "different triggers must produce separate bindings");
        TestAssertions.assertTrue(bindings.actionFor(GestureShortcutTrigger.doubleTap()).isOpenMenu(), "double-tap binding must remain available");
        TestAssertions.assertTrue(bindings.actionFor(GestureShortcutTrigger.circle()).isNextTab(), "circle binding must remain available");
    }

    @Test
    void emptyBindingsHaveNoShapeGestureShortcuts() {
        GestureShortcutBindings bindings = GestureShortcutBindings.empty();

        TestAssertions.assertFalse(bindings.hasShapeGestureShortcuts(), "empty bindings must not require shape gesture tracing");
    }

    @Test
    void doubleTapOnlyBindingsHaveNoShapeGestureShortcuts() {
        GestureShortcutBindings bindings = GestureShortcutBindings.empty()
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.doubleTap(), GestureShortcutAction.openMenu()));

        TestAssertions.assertFalse(bindings.hasShapeGestureShortcuts(), "double-tap must not require shape gesture tracing");
    }

    @Test
    void directionalBindingsHaveShapeGestureShortcutsEvenWhenCircleIsOff() {
        GestureShortcutBindings bindings = GestureShortcutBindings.empty()
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.circle(), GestureShortcutAction.off()))
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.swipeDown(), GestureShortcutAction.nextTab()));

        TestAssertions.assertTrue(bindings.hasShapeGestureShortcuts(), "directional gestures must keep shape gesture tracing active when circle is off");
    }

    @Test
    void customShapeBindingsHaveShapeGestureShortcuts() {
        GestureShortcutBindings bindings = GestureShortcutBindings.empty()
                .put(GestureShortcutBinding.of(GestureShortcutTrigger.customShape(), GestureShortcutAction.openMenu()));

        TestAssertions.assertTrue(bindings.hasShapeGestureShortcuts(), "custom gestures must keep shape gesture tracing active");
    }

    @Test
    void nullBindingIsRejected() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                GestureShortcutBindings.empty().put(null);
            }
        });
    }
}
