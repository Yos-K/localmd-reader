package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchaseStartResultTest {

    @Test
    void startedPurchaseFlowMovesUiToInProgress() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseStartResult.started().uiState());

        TestAssertions.assertEquals("purchase_in_progress", presentation.messageCode(), "Started purchase flow must move UI to in-progress");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Started purchase flow must hide purchase action");
    }

    @Test
    void unavailablePurchaseFlowMovesUiToUnavailable() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseStartResult.unavailable().uiState());

        TestAssertions.assertEquals("purchase_unavailable", presentation.messageCode(), "Unavailable purchase flow must move UI to unavailable");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Unavailable purchase flow must hide purchase action");
    }

    @Test
    void pendingPurchaseFlowMovesUiToPending() {
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), ProPurchaseStartResult.pending().uiState());

        TestAssertions.assertEquals("purchase_pending", presentation.messageCode(), "Pending purchase flow must move UI to pending");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Pending purchase flow must hide purchase action");
    }
}
