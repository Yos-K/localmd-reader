package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class BuildProPurchaseStatusProviderTest {

    @Test
    void currentBuildStatusProviderReportsBillingUnavailableUntilBillingProviderIsConnected() {
        RecordingProPurchaseStatusCallback callback = new RecordingProPurchaseStatusCallback();

        BuildProPurchaseStatusProvider.current().queryCurrentSnapshot(callback);
        BillingPurchaseSnapshot snapshot = callback.snapshot();

        TestAssertions.assertEquals(
                "billing_unavailable",
                snapshot.proPurchaseState().persistenceCode(),
                "Current build status provider must stay unavailable until a billing provider adapter is connected");
        TestAssertions.assertFalse(
                snapshot.proPurchaseState().entitlement().isPro(),
                "Unavailable purchase status provider must keep Free entitlement");
    }

    @Test
    void currentBuildStatusProviderCanAcceptAnUnavailableBillingProvider() {
        RecordingProPurchaseStatusCallback callback = new RecordingProPurchaseStatusCallback();
        ProPurchaseStatusProvider unavailableProvider = UnavailableProPurchaseStatusProvider.instance();

        BuildProPurchaseStatusProvider.current(unavailableProvider).queryCurrentSnapshot(callback);
        BillingPurchaseSnapshot snapshot = callback.snapshot();

        TestAssertions.assertEquals(
                "billing_unavailable",
                snapshot.proPurchaseState().persistenceCode(),
                "Build status provider must accept an unavailable Billing adapter without granting Pro");
        TestAssertions.assertFalse(
                snapshot.proPurchaseState().entitlement().isPro(),
                "Unavailable Billing adapter must keep Free entitlement");
    }

    private static final class RecordingProPurchaseStatusCallback implements ProPurchaseStatusCallback {
        private BillingPurchaseSnapshot snapshot;

        @Override
        public void onCurrentSnapshot(BillingPurchaseSnapshot snapshot) {
            this.snapshot = snapshot;
        }

        private BillingPurchaseSnapshot snapshot() {
            return snapshot;
        }
    }
}
