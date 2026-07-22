package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class FeatureEntitlementTest {

    @Test
    void freeEntitlementAllowsEveryFreeFeature() {
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
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.CODE_HIGHLIGHTING), "Free entitlement must allow basic code highlighting");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.MERMAID_RENDERING), "Free entitlement must allow basic Mermaid rendering");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.DOUBLE_TAP_SHORTCUTS), "Free entitlement must allow double tap shortcuts");
    }

    @Test
    void freeEntitlementDoesNotAllowProFeatures() {
        FeatureEntitlement entitlement = FeatureEntitlement.free();

        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Free entitlement must not allow extra themes");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS), "Free entitlement must not allow custom gestures");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.TABLE_READING_ENHANCEMENTS), "Free entitlement must not allow table reading enhancements");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTENDED_RECENT_FILES), "Free entitlement must not allow extended recent files");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.TABLE_OF_CONTENTS), "Free entitlement must not allow table of contents");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.HEADING_JUMP), "Free entitlement must not allow heading jump");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.RELATIVE_LINKS), "Free entitlement must not allow relative links");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.RELATIVE_IMAGES), "Free entitlement must not allow relative images");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXPORT_OPTIONS), "Free entitlement must not allow export options");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.PROJECT_LIBRARY), "Free entitlement must not allow nested project library navigation");
    }

    @Test
    void proEntitlementAllowsEveryFreeAndProFeature() {
        FeatureEntitlement entitlement = FeatureEntitlement.pro();

        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.OPEN_LOCAL_MARKDOWN), "Pro entitlement must allow local Markdown opening");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.OPEN_FROM_TERMUX), "Pro entitlement must allow Termux opening");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.LIGHT_AND_DARK_THEME), "Pro entitlement must allow light and dark theme");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.PINCH_FONT_SIZE), "Pro entitlement must allow pinch font size");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.TABS), "Pro entitlement must allow tabs");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.RESTORE_TABS), "Pro entitlement must allow tab restoration");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.RECENT_FILES_LIMITED), "Pro entitlement must allow limited recent files");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.CLEAR_RECENT_FILES), "Pro entitlement must allow clearing recent files");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.CONTROLS_PLACEMENT), "Pro entitlement must allow controls placement");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.ENGLISH_AND_JAPANESE_UI), "Pro entitlement must allow English and Japanese UI");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.PRIVACY_DIALOG), "Pro entitlement must allow privacy dialog");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.DOUBLE_TAP_SHORTCUTS), "Pro entitlement must allow double tap shortcuts");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Pro entitlement must allow extra themes");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.MERMAID_RENDERING), "Pro entitlement must allow Mermaid rendering");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.CODE_HIGHLIGHTING), "Pro entitlement must allow code highlighting");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS), "Pro entitlement must allow custom gestures");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.TABLE_READING_ENHANCEMENTS), "Pro entitlement must allow table reading enhancements");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.EXTENDED_RECENT_FILES), "Pro entitlement must allow extended recent files");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.TABLE_OF_CONTENTS), "Pro entitlement must allow table of contents");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.HEADING_JUMP), "Pro entitlement must allow heading jump");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.PROJECT_LIBRARY), "Pro entitlement must allow the persistent Markdown library");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.RELATIVE_LINKS), "Pro entitlement must allow relative links");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.RELATIVE_IMAGES), "Pro entitlement must allow relative images");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.EXPORT_OPTIONS), "Pro entitlement must allow export options");
    }

    @Test
    void unknownFeatureCannotBeCreated() {
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
