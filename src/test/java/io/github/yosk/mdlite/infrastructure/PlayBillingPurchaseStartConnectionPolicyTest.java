package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ProPurchaseStartResult;
import io.github.yosk.mdlite.domain.ProPurchasePresentation;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class PlayBillingPurchaseStartConnectionPolicyTest {
    private static final int BILLING_OK = 0;
    private static final int BILLING_SERVICE_UNAVAILABLE = 2;

    @Test
    void successfulBillingSetupMustContinueToProductDetailsQuery() {
        TestAssertions.assertTrue(
                PlayBillingPurchaseStartConnectionPolicy.shouldQueryProductDetailsAfterSetup(BILLING_OK),
                "Successful Billing setup must continue to product details query");
    }

    @Test
    void failedBillingSetupMustNotContinueToProductDetailsQuery() {
        TestAssertions.assertFalse(
                PlayBillingPurchaseStartConnectionPolicy.shouldQueryProductDetailsAfterSetup(BILLING_SERVICE_UNAVAILABLE),
                "Failed Billing setup must not query product details from an unavailable client");
    }

    @Test
    void disconnectedBillingSetupMustResolvePurchaseStartAsUnavailable() {
        ProPurchaseStartResult result =
                PlayBillingPurchaseStartConnectionPolicy.startResultAfterDisconnectedSetup();

        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), result.uiState());

        TestAssertions.assertEquals(
                "purchase_unavailable",
                presentation.messageCode(),
                "Disconnected Billing setup must resolve the waiting purchase start as unavailable");
    }
}
