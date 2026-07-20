package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.ProProduct;
import io.github.yosk.mdlite.domain.ProPurchaseFlowCallback;
import io.github.yosk.mdlite.domain.ProPurchasePresentation;
import io.github.yosk.mdlite.domain.ProPurchaseStartResult;
import io.github.yosk.mdlite.domain.UnavailableProPurchaseFlow;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class BuildProPurchaseFlowTest {

    @Test
    void currentBuildPurchaseFlowStaysUnavailableUntilBillingFlowIsConnected() {
        ProPurchaseStartResult result = BuildProPurchaseFlow.current()
                .start(ProProduct.pro(), new NoOpPurchaseFlowCallback());
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), result.uiState());

        TestAssertions.assertEquals(
                "purchase_unavailable",
                presentation.messageCode(),
                "Current build purchase flow must stay unavailable until a billing flow adapter is connected");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Unavailable build purchase flow must not expose a successful Pro action");
    }

    @Test
    void currentBuildPurchaseFlowRejectsMissingBillingFlowAdapter() {
        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override
                    public void run() {
                        BuildProPurchaseFlow.current(null);
                    }
                });
    }

    @Test
    void currentBuildPurchaseFlowCanAcceptAnUnavailablePurchaseFlowAdapter() {
        ProPurchaseStartResult result = BuildProPurchaseFlow.current(UnavailableProPurchaseFlow.instance())
                .start(ProProduct.pro(), new NoOpPurchaseFlowCallback());
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), result.uiState());

        TestAssertions.assertEquals(
                "purchase_unavailable",
                presentation.messageCode(),
                "Build purchase flow must accept an unavailable Billing adapter without granting Pro");
        TestAssertions.assertFalse(
                presentation.shouldShowAction(),
                "Unavailable Billing purchase flow must hide purchase actions after tap");
    }

    private static final class NoOpPurchaseFlowCallback implements ProPurchaseFlowCallback {
        @Override
        public void onPurchaseStartResolved(ProPurchaseStartResult result) {
        }
    }
}
