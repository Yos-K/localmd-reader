package io.github.yosk.mdlite.domain;

public final class FeatureEntitlementTest {
    public static void main(String[] args) {
        freeEntitlementAllowsEveryFreeFeature();
        freeEntitlementDoesNotAllowProFeatures();
        proEntitlementAllowsEveryFreeAndProFeature();
        unknownFeatureCannotBeCreated();
    }

    private static void freeEntitlementAllowsEveryFreeFeature() {
        FeatureEntitlement entitlement = FeatureEntitlement.free();

        assertTrue(entitlement.allows(ViewerFeature.OPEN_LOCAL_MARKDOWN));
        assertTrue(entitlement.allows(ViewerFeature.OPEN_FROM_TERMUX));
        assertTrue(entitlement.allows(ViewerFeature.LIGHT_AND_DARK_THEME));
        assertTrue(entitlement.allows(ViewerFeature.PINCH_FONT_SIZE));
        assertTrue(entitlement.allows(ViewerFeature.TABS));
        assertTrue(entitlement.allows(ViewerFeature.RESTORE_TABS));
        assertTrue(entitlement.allows(ViewerFeature.RECENT_FILES_LIMITED));
        assertTrue(entitlement.allows(ViewerFeature.CLEAR_RECENT_FILES));
        assertTrue(entitlement.allows(ViewerFeature.CONTROLS_PLACEMENT));
        assertTrue(entitlement.allows(ViewerFeature.ENGLISH_AND_JAPANESE_UI));
        assertTrue(entitlement.allows(ViewerFeature.PRIVACY_DIALOG));
    }

    private static void freeEntitlementDoesNotAllowProFeatures() {
        FeatureEntitlement entitlement = FeatureEntitlement.free();

        assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES));
        assertFalse(entitlement.allows(ViewerFeature.MERMAID_RENDERING));
        assertFalse(entitlement.allows(ViewerFeature.CODE_HIGHLIGHTING));
        assertFalse(entitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS));
        assertFalse(entitlement.allows(ViewerFeature.TABLE_READING_ENHANCEMENTS));
        assertFalse(entitlement.allows(ViewerFeature.EXTENDED_RECENT_FILES));
        assertFalse(entitlement.allows(ViewerFeature.TABLE_OF_CONTENTS));
        assertFalse(entitlement.allows(ViewerFeature.HEADING_JUMP));
        assertFalse(entitlement.allows(ViewerFeature.FOLDER_BROWSING));
        assertFalse(entitlement.allows(ViewerFeature.RELATIVE_LINKS));
        assertFalse(entitlement.allows(ViewerFeature.RELATIVE_IMAGES));
        assertFalse(entitlement.allows(ViewerFeature.EXPORT_OPTIONS));
    }

    private static void proEntitlementAllowsEveryFreeAndProFeature() {
        FeatureEntitlement entitlement = FeatureEntitlement.pro();

        for (ViewerFeature feature : ViewerFeature.values()) {
            assertTrue(entitlement.allows(feature));
        }
    }

    private static void unknownFeatureCannotBeCreated() {
        assertThrows(
                IllegalArgumentException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() {
                        ViewerFeature.fromId("unknown-feature");
                    }
                });
    }

    private static void assertTrue(boolean actual) {
        if (!actual) {
            throw new AssertionError("Expected true.");
        }
    }

    private static void assertFalse(boolean actual) {
        if (actual) {
            throw new AssertionError("Expected false.");
        }
    }

    private static void assertThrows(
            Class<? extends RuntimeException> expected,
            ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException actual) {
            if (expected.isInstance(actual)) {
                return;
            }
            throw new AssertionError("Unexpected exception: " + actual);
        }
        throw new AssertionError("Expected exception: " + expected.getName());
    }

    private interface ThrowingRunnable {
        void run();
    }
}
