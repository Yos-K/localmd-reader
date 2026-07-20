package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class GestureShortcutBindingTest {

    @Test
    void bindingRequiresTrigger() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                GestureShortcutBinding.of(null, GestureShortcutAction.openMenu());
            }
        });
    }

    @Test
    void bindingRequiresAction() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                GestureShortcutBinding.of(GestureShortcutTrigger.circle(), null);
            }
        });
    }

    @Test
    void bindingKeepsTriggerAndActionTogether() {
        GestureShortcutBinding binding = GestureShortcutBinding.of(
                GestureShortcutTrigger.circle(),
                GestureShortcutAction.openMenu());

        TestAssertions.assertTrue(binding.trigger().isCircle(), "binding must keep the trigger");
        TestAssertions.assertTrue(binding.action().isOpenMenu(), "binding must keep the action");
    }
}
