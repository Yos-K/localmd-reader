package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ProPurchaseStartResult;

public final class PlayBillingPurchaseStartConnectionPolicy {
    private static final int BILLING_OK = 0;

    private PlayBillingPurchaseStartConnectionPolicy() {
    }

    public static boolean shouldQueryProductDetailsAfterSetup(int billingResponseCode) {
        return billingResponseCode == BILLING_OK;
    }

    public static ProPurchaseStartResult startResultAfterDisconnectedSetup() {
        return ProPurchaseStartResult.unavailable();
    }
}
