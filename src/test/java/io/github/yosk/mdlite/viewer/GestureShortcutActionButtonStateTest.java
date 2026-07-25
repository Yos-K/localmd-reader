package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class GestureShortcutActionButtonStateTest {
    @Nested
    final class AssignmentMeaning {
        @Test
        void actionAssignedToAnotherGestureIsMuted() {
            GestureShortcutBindings bindings = GestureShortcutBindings.empty().put(GestureShortcutBinding.of(
                    GestureShortcutTrigger.circle(), GestureShortcutAction.openFile()));

            GestureShortcutActionButtonState state = GestureShortcutActionButtonState.forSelection(
                    bindings, GestureShortcutTrigger.doubleTap(), GestureShortcutAction.openFile());

            TestAssertions.assertTrue(state.isMuted(),
                    "an action owned by another gesture must communicate that selection will move it");
        }

        @Test
        void actionAssignedToTheSelectedGestureRemainsEmphasized() {
            GestureShortcutBindings bindings = GestureShortcutBindings.empty().put(GestureShortcutBinding.of(
                    GestureShortcutTrigger.doubleTap(), GestureShortcutAction.openFile()));

            GestureShortcutActionButtonState state = GestureShortcutActionButtonState.forSelection(
                    bindings, GestureShortcutTrigger.doubleTap(), GestureShortcutAction.openFile());

            TestAssertions.assertFalse(
                    state.isMuted(), "the selected gesture's current action must remain visually available");
        }

        @Test
        void unassignedActionRemainsEmphasized() {
            GestureShortcutActionButtonState state = GestureShortcutActionButtonState.forSelection(
                    GestureShortcutBindings.empty(), GestureShortcutTrigger.doubleTap(), GestureShortcutAction.openFile());

            TestAssertions.assertFalse(state.isMuted(), "an unassigned action must remain visually available");
        }
    }

    @Nested
    final class SafeDefaults {
        @Test
        void missingInputsProduceAnAvailableState() {
            GestureShortcutActionButtonState state =
                    GestureShortcutActionButtonState.forSelection(null, null, null);

            TestAssertions.assertFalse(state.isMuted(), "missing optional state must fail safe as available");
        }
    }
}
