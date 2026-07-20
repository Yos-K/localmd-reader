package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchasePresentationTest {

    @Test
    void proEntitlementHidesPurchaseAction() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.pro(), ProPurchaseUiState.ready());

        TestAssertions.assertFalse(presentation.shouldShowAction(), "Pro entitlement must hide purchase action");
        TestAssertions.assertFalse(presentation.shouldShowRestoreAction(), "Pro entitlement must hide restore action");
        TestAssertions.assertEquals("pro_active", presentation.messageCode(), "Pro entitlement must show active status");
    }

    @Test
    void freeEntitlementShowsPurchaseActionWhenBillingIsReady() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseUiState.ready());

        TestAssertions.assertTrue(presentation.shouldShowAction(), "Free entitlement with ready billing must show purchase action");
        TestAssertions.assertTrue(presentation.shouldShowRestoreAction(), "Free entitlement with ready billing must show restore action");
        TestAssertions.assertEquals("purchase_available", presentation.messageCode(), "Free entitlement with ready billing must show purchase available message");
    }

    @Test
    void freeEntitlementShowsUnavailableMessageWhenBillingIsUnavailable() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseUiState.unavailable());

        TestAssertions.assertFalse(presentation.shouldShowAction(), "Unavailable billing must hide purchase action");
        TestAssertions.assertFalse(presentation.shouldShowRestoreAction(), "Unavailable billing must hide restore action");
        TestAssertions.assertEquals("purchase_unavailable", presentation.messageCode(), "Unavailable billing must show unavailable message");
    }

    @Test
    void pendingPurchaseShowsPendingMessageWithoutPurchaseAction() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseUiState.pending());

        TestAssertions.assertFalse(presentation.shouldShowAction(), "Pending purchase must hide purchase action");
        TestAssertions.assertFalse(presentation.shouldShowRestoreAction(), "Pending purchase must hide restore action");
        TestAssertions.assertEquals("purchase_pending", presentation.messageCode(), "Pending purchase must show pending message");
    }

    @Test
    void purchaseInProgressShowsProgressMessageWithoutPurchaseAction() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseUiState.inProgress());

        TestAssertions.assertFalse(presentation.shouldShowAction(), "In-progress purchase must hide purchase action");
        TestAssertions.assertFalse(presentation.shouldShowRestoreAction(), "In-progress purchase must hide restore action");
        TestAssertions.assertEquals("purchase_in_progress", presentation.messageCode(), "In-progress purchase must show progress message");
    }
}
