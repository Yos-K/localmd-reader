package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchaseStartResultTest {

    @Test
    void startedPurchaseFlowMovesUiToInProgress() {
        ProPurchaseStartResult result = ProPurchaseStartResult.started();
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), result.uiState());

        TestAssertions.assertEquals("purchase_in_progress", presentation.messageCode(), "Started purchase flow must move UI to in-progress");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Started purchase flow must hide purchase action");
        TestAssertions.assertFalse(result.shouldShowMessageDialog(),
                "The Google Play purchase sheet must be the only modal surface while purchase is opening");
    }

    @Test
    void unavailablePurchaseFlowMovesUiToUnavailable() {
        ProPurchaseStartResult result = ProPurchaseStartResult.unavailable();
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), result.uiState());

        TestAssertions.assertEquals("purchase_unavailable", presentation.messageCode(), "Unavailable purchase flow must move UI to unavailable");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Unavailable purchase flow must hide purchase action");
        TestAssertions.assertTrue(result.shouldShowMessageDialog(),
                "A purchase start failure must explain that purchasing is unavailable");
    }

    @Test
    void pendingPurchaseFlowMovesUiToPending() {
        ProPurchaseStartResult result = ProPurchaseStartResult.pending();
        ProPurchasePresentation presentation =
                ProPurchasePresentation.from(FeatureEntitlement.free(), result.uiState());

        TestAssertions.assertEquals("purchase_pending", presentation.messageCode(), "Pending purchase flow must move UI to pending");
        TestAssertions.assertFalse(presentation.shouldShowAction(), "Pending purchase flow must hide purchase action");
        TestAssertions.assertTrue(result.shouldShowMessageDialog(),
                "A pending purchase must explain that completion is still pending");
    }
}
