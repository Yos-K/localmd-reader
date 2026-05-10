package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class FeatureEntitlementsTest {
    public static void main(String[] args) {
        currentClosedTestingReleaseUsesFreeEntitlement();
        currentClosedTestingReleaseKeepsProFeaturesLocked();
        currentUsesProvidedProEntitlementSource();
        missingEntitlementSourceFallsBackToFreeEntitlement();
    }

    private static void currentClosedTestingReleaseUsesFreeEntitlement() {
        FeatureEntitlement entitlement = FeatureEntitlements.currentClosedTestingRelease();

        TestAssertions.assertFalse(entitlement.isPro(), "Closed testing entitlement must not be Pro");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.OPEN_LOCAL_MARKDOWN), "Closed testing entitlement must allow opening local Markdown");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.TABS), "Closed testing entitlement must allow tabs");
    }

    private static void currentClosedTestingReleaseKeepsProFeaturesLocked() {
        FeatureEntitlement entitlement = FeatureEntitlements.currentClosedTestingRelease();

        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.CODE_HIGHLIGHTING), "Closed testing entitlement must keep code highlighting locked");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Closed testing entitlement must keep extra themes locked");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.MERMAID_RENDERING), "Closed testing entitlement must keep Mermaid rendering locked");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS), "Closed testing entitlement must keep custom gesture shortcuts locked");
    }

    private static void currentUsesProvidedProEntitlementSource() {
        FeatureEntitlement entitlement = FeatureEntitlements.current(StaticEntitlementSource.pro());

        TestAssertions.assertTrue(entitlement.isPro(), "Current entitlement must use the provided Pro source");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.CODE_HIGHLIGHTING), "Provided Pro source must unlock code highlighting");
    }

    private static void missingEntitlementSourceFallsBackToFreeEntitlement() {
        FeatureEntitlement entitlement = FeatureEntitlements.current(null);

        TestAssertions.assertFalse(entitlement.isPro(), "Missing entitlement source must fall back to Free");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.CODE_HIGHLIGHTING), "Missing entitlement source must keep Pro features locked");
    }
}
