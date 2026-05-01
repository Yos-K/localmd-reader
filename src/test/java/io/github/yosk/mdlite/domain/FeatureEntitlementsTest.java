package io.github.yosk.mdlite.domain;

public final class FeatureEntitlementsTest {
    public static void main(String[] args) {
        currentClosedTestingReleaseUsesFreeEntitlement();
        currentClosedTestingReleaseKeepsProFeaturesLocked();
    }

    private static void currentClosedTestingReleaseUsesFreeEntitlement() {
        FeatureEntitlement entitlement = FeatureEntitlements.currentClosedTestingRelease();

        assertFalse(entitlement.isPro());
        assertTrue(entitlement.allows(ViewerFeature.OPEN_LOCAL_MARKDOWN));
        assertTrue(entitlement.allows(ViewerFeature.TABS));
    }

    private static void currentClosedTestingReleaseKeepsProFeaturesLocked() {
        FeatureEntitlement entitlement = FeatureEntitlements.currentClosedTestingRelease();

        assertFalse(entitlement.allows(ViewerFeature.CODE_HIGHLIGHTING));
        assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES));
        assertFalse(entitlement.allows(ViewerFeature.MERMAID_RENDERING));
        assertFalse(entitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS));
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
}
