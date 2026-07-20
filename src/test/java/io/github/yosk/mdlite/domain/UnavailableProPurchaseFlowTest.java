package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class UnavailableProPurchaseFlowTest {

    @Test
    void unavailableFlowReturnsUnavailablePurchaseStartResult() {
        RecordingProPurchaseFlowCallback callback = new RecordingProPurchaseFlowCallback();
        ProPurchaseStartResult result = UnavailableProPurchaseFlow.instance().start(ProProduct.pro(), callback);
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), result.uiState());

        TestAssertions.assertEquals("purchase_unavailable", presentation.messageCode(), "Unavailable purchase flow must return unavailable result");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Unavailable purchase flow must hide purchase action after tap");
    }

    @Test
    void unavailableFlowAcceptsTheProProductWithoutGrantingEntitlement() {
        RecordingProPurchaseFlowCallback callback = new RecordingProPurchaseFlowCallback();
        ProPurchaseStartResult result = UnavailableProPurchaseFlow.instance().start(ProProduct.pro(), callback);
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), result.uiState());

        TestAssertions.assertFalse(presentation.shouldShowAction(), "Unavailable purchase flow must not expose a successful Pro action");
    }

    @Test
    void unavailableFlowDoesNotNotifyDelayedPurchaseStartResult() {
        RecordingProPurchaseFlowCallback callback = new RecordingProPurchaseFlowCallback();

        UnavailableProPurchaseFlow.instance().start(ProProduct.pro(), callback);

        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), callback.result().uiState());
        TestAssertions.assertEquals("purchase_in_progress", presentation.messageCode(), "Unavailable purchase flow must return the immediate result instead of emitting a delayed result");
    }

    private static final class RecordingProPurchaseFlowCallback implements ProPurchaseFlowCallback {
        private ProPurchaseStartResult result = ProPurchaseStartResult.started();

        @Override
        public void onPurchaseStartResolved(ProPurchaseStartResult result) {
            this.result = result;
        }

        private ProPurchaseStartResult result() {
            return result;
        }
    }
}
