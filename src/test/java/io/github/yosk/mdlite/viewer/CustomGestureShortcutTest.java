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

    @Test
    void validStoredShapeAndActionRestoreOneCompleteShortcut() {
        CustomGestureShortcut shortcut = CustomGestureShortcut
                .restore(shape().storedValue(), "next_tab").get();

        TestAssertions.assertTrue(shortcut.action().isNextTab(),
                "a complete persisted custom gesture must restore its action");
        TestAssertions.assertEquals(0,
                Math.round(shortcut.shape().averageDistanceTo(shape()) * 1000f),
                "a complete persisted custom gesture must restore its shape");
    }

    @Test
    void missingStoredShapeRestoresNoShortcut() {
        TestAssertions.assertFalse(CustomGestureShortcut.restore("", "next_tab").isPresent(),
                "an action without a shape must not become a registered custom gesture");
    }

    @Test
    void unknownStoredActionRestoresNoShortcut() {
        TestAssertions.assertFalse(
                CustomGestureShortcut.restore(shape().storedValue(), "unknown_action").isPresent(),
                "a shape with an unknown action must not become a registered custom gesture");
    }

    @Test
    void malformedStoredShapeRestoresNoShortcut() {
        TestAssertions.assertFalse(CustomGestureShortcut.restore("not-a-shape", "next_tab").isPresent(),
                "malformed persisted coordinates must fail closed without an exception");
    }

    @Test
    void nonFiniteStoredShapeRestoresNoShortcut() {
        String corrupted = shape().storedValue().replaceFirst("-?[0-9]+\\.[0-9]+", "NaN");

        TestAssertions.assertFalse(CustomGestureShortcut.restore(corrupted, "next_tab").isPresent(),
                "non-finite persisted coordinates must fail closed without an exception");
    }

    private static CustomGestureShape shape() {
        return CustomGestureShape.fromPoints(
                new float[] { 10f, 60f, 110f, 160f },
                new float[] { 100f, 40f, 100f, 40f });
    }
}
