package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class FeatureEntitlementTest {
    public static void main(String[] args) {
        freeEntitlementAllowsEveryFreeFeature();
        freeEntitlementDoesNotAllowProFeatures();
        proEntitlementAllowsEveryFreeAndProFeature();
        unknownFeatureCannotBeCreated();
    }

    private static void freeEntitlementAllowsEveryFreeFeature() {
        FeatureEntitlement entitlement = FeatureEntitlement.free();

        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.OPEN_LOCAL_MARKDOWN), "Free entitlement must allow local Markdown opening");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.OPEN_FROM_TERMUX), "Free entitlement must allow Termux opening");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.LIGHT_AND_DARK_THEME), "Free entitlement must allow light and dark theme");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.PINCH_FONT_SIZE), "Free entitlement must allow pinch font size");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.TABS), "Free entitlement must allow tabs");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.RESTORE_TABS), "Free entitlement must allow tab restoration");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.RECENT_FILES_LIMITED), "Free entitlement must allow limited recent files");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.CLEAR_RECENT_FILES), "Free entitlement must allow clearing recent files");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.CONTROLS_PLACEMENT), "Free entitlement must allow controls placement");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.ENGLISH_AND_JAPANESE_UI), "Free entitlement must allow English and Japanese UI");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.PRIVACY_DIALOG), "Free entitlement must allow privacy dialog");
    }

    private static void freeEntitlementDoesNotAllowProFeatures() {
        FeatureEntitlement entitlement = FeatureEntitlement.free();

        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Free entitlement must not allow extra themes");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.MERMAID_RENDERING), "Free entitlement must not allow Mermaid rendering");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.CODE_HIGHLIGHTING), "Free entitlement must not allow code highlighting");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS), "Free entitlement must not allow custom gestures");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.TABLE_READING_ENHANCEMENTS), "Free entitlement must not allow table reading enhancements");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTENDED_RECENT_FILES), "Free entitlement must not allow extended recent files");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.TABLE_OF_CONTENTS), "Free entitlement must not allow table of contents");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.HEADING_JUMP), "Free entitlement must not allow heading jump");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.FOLDER_BROWSING), "Free entitlement must not allow folder browsing");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.RELATIVE_LINKS), "Free entitlement must not allow relative links");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.RELATIVE_IMAGES), "Free entitlement must not allow relative images");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXPORT_OPTIONS), "Free entitlement must not allow export options");
    }

    private static void proEntitlementAllowsEveryFreeAndProFeature() {
        FeatureEntitlement entitlement = FeatureEntitlement.pro();

        for (ViewerFeature feature : ViewerFeature.values()) {
            TestAssertions.assertTrue(entitlement.allows(feature), "Pro entitlement must allow every viewer feature");
        }
    }

    private static void unknownFeatureCannotBeCreated() {
        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override
                    public void run() {
                        ViewerFeature.fromId("unknown-feature");
                    }
                });
    }
}
