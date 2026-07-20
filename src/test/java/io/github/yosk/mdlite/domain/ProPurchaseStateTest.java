package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchaseStateTest {

    @Test
    void purchasedStateGrantsProEntitlement() {
        FeatureEntitlement entitlement = ProPurchaseState.purchased().entitlement();

        TestAssertions.assertTrue(entitlement.isPro(), "Purchased Pro state must grant Pro entitlement");
        TestAssertions.assertTrue(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Purchased Pro state must unlock convenience features");
    }

    @Test
    void notPurchasedStateKeepsFreeEntitlement() {
        FeatureEntitlement entitlement = ProPurchaseState.notPurchased().entitlement();

        TestAssertions.assertFalse(entitlement.isPro(), "Not-purchased Pro state must keep Free entitlement");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Not-purchased Pro state must keep convenience features locked");
    }

    @Test
    void pendingStateKeepsFreeEntitlement() {
        FeatureEntitlement entitlement = ProPurchaseState.pending().entitlement();

        TestAssertions.assertFalse(entitlement.isPro(), "Pending Pro purchase must keep Free entitlement until purchase is complete");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Pending Pro purchase must keep convenience features locked");
    }

    @Test
    void unknownStateKeepsFreeEntitlement() {
        FeatureEntitlement entitlement = ProPurchaseState.unknown().entitlement();

        TestAssertions.assertFalse(entitlement.isPro(), "Unknown Pro purchase state must fail safe as Free");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Unknown Pro purchase state must keep convenience features locked");
    }

    @Test
    void billingUnavailableStateKeepsFreeEntitlement() {
        FeatureEntitlement entitlement = ProPurchaseState.billingUnavailable().entitlement();

        TestAssertions.assertFalse(entitlement.isPro(), "Billing-unavailable Pro state must fail safe as Free");
        TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES), "Billing-unavailable Pro state must keep convenience features locked");
    }

    @Test
    void purchasedStateCanBeRestoredFromPersistenceCode() {
        ProPurchaseState state = ProPurchaseState.fromPersistenceCode(ProPurchaseState.purchased().persistenceCode());

        TestAssertions.assertTrue(state.entitlement().isPro(), "Purchased persistence code must restore purchased Pro state");
    }

    @Test
    void notPurchasedStateCanBeRestoredFromPersistenceCode() {
        ProPurchaseState state = ProPurchaseState.fromPersistenceCode(ProPurchaseState.notPurchased().persistenceCode());

        TestAssertions.assertFalse(state.entitlement().isPro(), "Not-purchased persistence code must restore Free entitlement");
    }

    @Test
    void pendingStateCanBeRestoredFromPersistenceCode() {
        ProPurchaseState state = ProPurchaseState.fromPersistenceCode(ProPurchaseState.pending().persistenceCode());

        TestAssertions.assertFalse(state.entitlement().isPro(), "Pending persistence code must restore Free entitlement");
    }

    @Test
    void unknownStateCanBeRestoredFromPersistenceCode() {
        ProPurchaseState state = ProPurchaseState.fromPersistenceCode(ProPurchaseState.unknown().persistenceCode());

        TestAssertions.assertFalse(state.entitlement().isPro(), "Unknown persistence code must restore Free entitlement");
    }

    @Test
    void billingUnavailableStateCanBeRestoredFromPersistenceCode() {
        ProPurchaseState state = ProPurchaseState.fromPersistenceCode(ProPurchaseState.billingUnavailable().persistenceCode());

        TestAssertions.assertFalse(state.entitlement().isPro(), "Billing-unavailable persistence code must restore Free entitlement");
    }

    @Test
    void unknownPersistenceCodeFailsSafeAsUnknownState() {
        ProPurchaseState state = ProPurchaseState.fromPersistenceCode("purchase-token-like-value");

        TestAssertions.assertEquals(ProPurchaseState.unknown().persistenceCode(), state.persistenceCode(), "Unexpected persistence code must become unknown");
        TestAssertions.assertFalse(state.entitlement().isPro(), "Unexpected persistence code must fail safe as Free");
    }

    @Test
    void emptyPersistenceCodeFailsSafeAsUnknownState() {
        ProPurchaseState state = ProPurchaseState.fromPersistenceCode("");

        TestAssertions.assertEquals(ProPurchaseState.unknown().persistenceCode(), state.persistenceCode(), "Empty persistence code must become unknown");
        TestAssertions.assertFalse(state.entitlement().isPro(), "Empty persistence code must fail safe as Free");
    }
}
