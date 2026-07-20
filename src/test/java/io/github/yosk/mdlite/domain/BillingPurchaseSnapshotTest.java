package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class BillingPurchaseSnapshotTest {

    @Test
    void acknowledgedPurchasedProductMapsToPurchasedState() {
        ProPurchaseState state = BillingPurchaseSnapshot.purchased(true).proPurchaseState();

        TestAssertions.assertTrue(
                state.entitlement().isPro(),
                "Acknowledged purchased product must map to purchased Pro state");
    }

    @Test
    void unacknowledgedPurchasedProductMapsToPendingState() {
        ProPurchaseState state = BillingPurchaseSnapshot.purchased(false).proPurchaseState();

        TestAssertions.assertFalse(
                state.entitlement().isPro(),
                "Unacknowledged purchased product must stay Free until acknowledged");
    }

    @Test
    void pendingProductMapsToPendingState() {
        ProPurchaseState state = BillingPurchaseSnapshot.pending().proPurchaseState();

        TestAssertions.assertFalse(
                state.entitlement().isPro(),
                "Pending product must map to pending Pro state and keep Free entitlement");
    }

    @Test
    void missingProductMapsToNotPurchasedState() {
        ProPurchaseState state = BillingPurchaseSnapshot.notPurchased().proPurchaseState();

        TestAssertions.assertFalse(
                state.entitlement().isPro(),
                "Missing Pro product must map to not-purchased state");
    }

    @Test
    void unavailableBillingMapsToBillingUnavailableState() {
        ProPurchaseState state = BillingPurchaseSnapshot.billingUnavailable().proPurchaseState();

        TestAssertions.assertFalse(
                state.entitlement().isPro(),
                "Billing unavailable must map to Free entitlement");
    }
}
