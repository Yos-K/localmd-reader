package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.testing.TestAssertions;

public final class BuildEntitlementSourceTest {
    public static void main(String[] args) {
        currentBuildEntitlementDefaultsToFreeForNormalTestBuilds();
    }

    private static void currentBuildEntitlementDefaultsToFreeForNormalTestBuilds() {
        FeatureEntitlement entitlement = BuildEntitlementSource.current().currentEntitlement();

        TestAssertions.assertFalse(entitlement.isPro(), "Normal test builds must default to Free entitlement");
    }
}
