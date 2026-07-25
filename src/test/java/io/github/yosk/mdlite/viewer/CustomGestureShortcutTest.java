package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class CustomGestureShortcutTest {

    @Nested
    final class CreationValidity {
        @Test
        void missingShapeIsRejected() {
            TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
                @Override
                public void run() {
                    CustomGestureShortcut.of(null, GestureShortcutAction.openMenu());
                }
            });
        }

        @Test
        void inactiveActionIsRejected() {
            TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
                @Override
                public void run() {
                    CustomGestureShortcut.of(shape(), GestureShortcutAction.off());
                }
            });
        }
    }

    @Nested
    final class Binding {
        @Test
        void shortcutCreatesACustomShapeTrigger() {
            CustomGestureShortcut shortcut = CustomGestureShortcut.of(
                    shape(), GestureShortcutAction.nextTab());

            TestAssertions.assertTrue(shortcut.binding().trigger().isCustomShape(),
                    "custom gesture shortcut must create a custom shape trigger");
        }

        @Test
        void shortcutBindingKeepsTheConfiguredAction() {
            CustomGestureShortcut shortcut = CustomGestureShortcut.of(
                    shape(), GestureShortcutAction.nextTab());

            TestAssertions.assertTrue(shortcut.binding().action().isNextTab(),
                    "custom gesture shortcut binding must keep the configured action");
        }
    }

    @Nested
    final class Restoration {
        @Test
        void completeStoredValuesRestoreTheAction() {
            CustomGestureShortcut shortcut = CustomGestureShortcut
                    .restore(shape().storedValue(), "next_tab").get();

            TestAssertions.assertTrue(shortcut.action().isNextTab(),
                    "a complete persisted custom gesture must restore its action");
        }

        @Test
        void completeStoredValuesRestoreTheShape() {
            CustomGestureShortcut shortcut = CustomGestureShortcut
                    .restore(shape().storedValue(), "next_tab").get();

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
    }

    private static CustomGestureShape shape() {
        return CustomGestureShape.fromPoints(
                new float[] { 10f, 60f, 110f, 160f },
                new float[] { 100f, 40f, 100f, 40f });
    }
}
