package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class GestureShortcutActionLabelsTest {
    @Test
    void assignedActionShowsTheGestureThatCurrentlyOwnsIt() {
        String[] labels = GestureShortcutActionLabels.from(
                new GestureShortcutAction[] { GestureShortcutAction.openMenu() },
                GestureShortcutBindings.empty().put(GestureShortcutBinding.of(
                        GestureShortcutTrigger.circle(), GestureShortcutAction.openMenu())),
                ViewerText.fromLanguage(ViewerLanguage.english()));

        TestAssertions.assertEquals("Open menu (assigned to Circle)", labels[0],
                "action choices must expose their current gesture assignment");
    }

    @Test
    void unassignedActionKeepsItsCompactLabel() {
        String[] labels = GestureShortcutActionLabels.from(
                new GestureShortcutAction[] { GestureShortcutAction.nextTab() },
                GestureShortcutBindings.empty(),
                ViewerText.fromLanguage(ViewerLanguage.english()));

        TestAssertions.assertEquals("Next tab", labels[0],
                "an unassigned action must not claim a gesture owner");
    }
}
