package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CustomGestureShortcutTest {

    @Test
    void shortcutRequiresShape() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                CustomGestureShortcut.of(null, GestureShortcutAction.openMenu());
            }
        });
    }

    @Test
    void shortcutRequiresActiveAction() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                CustomGestureShortcut.of(shape(), GestureShortcutAction.off());
            }
        });
    }

    @Test
    void shortcutCreatesCustomShapeBinding() {
        CustomGestureShortcut shortcut = CustomGestureShortcut.of(shape(), GestureShortcutAction.nextTab());

        TestAssertions.assertTrue(shortcut.binding().trigger().isCustomShape(), "custom gesture shortcut must create a custom shape binding");
        TestAssertions.assertTrue(shortcut.binding().action().isNextTab(), "custom gesture shortcut binding must keep the configured action");
    }

    private static CustomGestureShape shape() {
        return CustomGestureShape.fromPoints(
                new float[] { 10f, 60f, 110f, 160f },
                new float[] { 100f, 40f, 100f, 40f });
    }
}
