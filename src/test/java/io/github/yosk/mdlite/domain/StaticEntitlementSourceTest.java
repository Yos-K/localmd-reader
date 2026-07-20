package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class StaticEntitlementSourceTest {

    @Test
    void freeSourceReturnsFreeEntitlement() {
        FeatureEntitlement entitlement = StaticEntitlementSource.free().currentEntitlement();

        TestAssertions.assertFalse(entitlement.isPro(), "Free static entitlement source must return Free entitlement");
    }

    @Test
    void proSourceReturnsProEntitlement() {
        FeatureEntitlement entitlement = StaticEntitlementSource.pro().currentEntitlement();

        TestAssertions.assertTrue(entitlement.isPro(), "Pro static entitlement source must return Pro entitlement");
    }

    @Test
    void missingEntitlementSourceFallsBackToFreeEntitlement() {
        FeatureEntitlement entitlement = StaticEntitlementSource.of(null).currentEntitlement();

        TestAssertions.assertFalse(entitlement.isPro(), "Missing static entitlement value must fall back to Free entitlement");
    }
}
