package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class PlayBillingConnectionResultPolicyTest {
    private static final int BILLING_OK = 0;
    private static final int BILLING_SERVICE_UNAVAILABLE = 2;

    @Test
    void successfulBillingSetupMustContinueToPurchaseQuery() {
        TestAssertions.assertTrue(
                PlayBillingConnectionResultPolicy.shouldQueryPurchasesAfterSetup(BILLING_OK),
                "Successful Billing setup must continue to current purchase query");
    }

    @Test
    void failedBillingSetupMustNotContinueToPurchaseQuery() {
        TestAssertions.assertFalse(
                PlayBillingConnectionResultPolicy.shouldQueryPurchasesAfterSetup(BILLING_SERVICE_UNAVAILABLE),
                "Failed Billing setup must not query purchases from an unavailable client");
    }

    @Test
    void disconnectedBillingSetupMustResolveAsBillingUnavailableSnapshot() {
        BillingPurchaseSnapshot snapshot =
                PlayBillingConnectionResultPolicy.statusSnapshotAfterDisconnectedSetup();

        TestAssertions.assertEquals(
                "billing_unavailable",
                snapshot.proPurchaseState().persistenceCode(),
                "Disconnected Billing setup must resolve the waiting status query as billing unavailable");
    }
}
