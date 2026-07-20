package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchaseUiStateTest {

    @Test
    void missingUiStateFallsBackToUnavailablePresentation() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseUiState.safe(null));

        TestAssertions.assertEquals("purchase_unavailable", presentation.messageCode(), "Missing purchase UI state must become unavailable");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Missing purchase UI state must not show purchase action");
    }

    @Test
    void readyUiStateCanBeReusedAsSafeState() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseUiState.safe(ProPurchaseUiState.ready()));

        TestAssertions.assertEquals("purchase_available", presentation.messageCode(), "Ready purchase UI state must stay ready");
        TestAssertions.assertTrue(presentation.shouldShowAction(), "Ready purchase UI state must show purchase action");
    }

    @Test
    void pendingPurchaseStateBecomesPendingUiState() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseUiState.fromPurchaseState(ProPurchaseState.pending()));

        TestAssertions.assertEquals("purchase_pending", presentation.messageCode(), "Pending purchase state must become pending UI state");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Pending purchase state must not show purchase action");
    }

    @Test
    void purchasedPurchaseStateBecomesReadyUiStateBecauseEntitlementControlsActiveStatus() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.pro(), ProPurchaseUiState.fromPurchaseState(ProPurchaseState.purchased()));

        TestAssertions.assertEquals("pro_active", presentation.messageCode(), "Purchased state must let Pro entitlement control active status");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Purchased state must not show purchase action");
    }

    @Test
    void notPurchasedPurchaseStateBecomesReadyUiStateSoPurchaseAndRestoreActionsCanBeShown() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseUiState.fromPurchaseState(ProPurchaseState.notPurchased()));

        TestAssertions.assertEquals("purchase_available", presentation.messageCode(), "Not-purchased state must become ready UI state");
        TestAssertions.assertTrue(presentation.shouldShowAction(), "Not-purchased ready state must show purchase action");
        TestAssertions.assertTrue(presentation.shouldShowRestoreAction(), "Not-purchased ready state must show restore action");
    }
}
