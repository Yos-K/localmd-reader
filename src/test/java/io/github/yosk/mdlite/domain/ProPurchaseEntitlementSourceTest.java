package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchaseEntitlementSourceTest {

    @Test
    void purchasedSourceGrantsProEntitlement() {
        EntitlementSource source = ProPurchaseEntitlementSource.from(ProPurchaseState.purchased());

        TestAssertions.assertTrue(
                source.currentEntitlement().isPro(),
                "Purchased Pro purchase source must grant Pro entitlement");
    }

    @Test
    void unknownSourceKeepsFreeEntitlement() {
        EntitlementSource source = ProPurchaseEntitlementSource.from(ProPurchaseState.unknown());

        TestAssertions.assertFalse(
                source.currentEntitlement().isPro(),
                "Unknown Pro purchase source must keep Free entitlement");
    }

    @Test
    void billingUnavailableSourceKeepsFreeEntitlement() {
        EntitlementSource source = ProPurchaseEntitlementSource.from(ProPurchaseState.billingUnavailable());

        TestAssertions.assertFalse(
                source.currentEntitlement().isPro(),
                "Billing-unavailable Pro purchase source must keep Free entitlement");
    }
}
