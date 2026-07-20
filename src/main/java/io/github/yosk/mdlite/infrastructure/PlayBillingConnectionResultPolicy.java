package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;

public final class PlayBillingConnectionResultPolicy {
    private static final int BILLING_OK = 0;

    private PlayBillingConnectionResultPolicy() {
    }

    public static BillingPurchaseSnapshot statusSnapshotAfterDisconnectedSetup() {
        return BillingPurchaseSnapshot.billingUnavailable();
    }

    public static boolean shouldQueryPurchasesAfterSetup(int billingResponseCode) {
        return billingResponseCode == BILLING_OK;
    }
}
