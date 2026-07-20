package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class FeatureEntitlementsTest {

    @Test
    void currentClosedTestingReleaseUsesFreeEntitlement() {
        FeatureEntitlement entitlement = FeatureEntitlements.currentClosedTestingRelease();

        TestAssertions.assertFalse(entitlement.isPro(), "Closed testing entitlement must not be Pro");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.OPEN_LOCAL_MARKDOWN), "Closed testing entitlement must allow opening local Markdown");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.TABS), "Closed testing entitlement must allow tabs");
    }

    @Test
    void currentClosedTestingReleaseAllowsCoreReaderEnhancements() {
        FeatureEntitlement entitlement = FeatureEntitlements.currentClosedTestingRelease();

        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.CODE_HIGHLIGHTING), "Closed testing entitlement must allow code highlighting");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.MERMAID_RENDERING), "Closed testing entitlement must allow Mermaid rendering");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.DOUBLE_TAP_SHORTCUTS), "Closed testing entitlement must allow double tap shortcuts");
    }

    @Test
    void currentClosedTestingReleaseKeepsConvenienceFeaturesLocked() {
        FeatureEntitlement entitlement = FeatureEntitlements.currentClosedTestingRelease();

        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Closed testing entitlement must keep extra themes locked");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS), "Closed testing entitlement must keep custom gesture shortcuts locked");
    }

    @Test
    void currentUsesProvidedProEntitlementSource() {
        FeatureEntitlement entitlement = FeatureEntitlements.current(StaticEntitlementSource.pro());

        TestAssertions.assertTrue(entitlement.isPro(), "Current entitlement must use the provided Pro source");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Provided Pro source must unlock convenience features");
    }

    @Test
    void missingEntitlementSourceFallsBackToFreeEntitlement() {
        FeatureEntitlement entitlement = FeatureEntitlements.current(null);

        TestAssertions.assertFalse(entitlement.isPro(), "Missing entitlement source must fall back to Free");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Missing entitlement source must keep Pro convenience features locked");
    }
}
