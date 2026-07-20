package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;

public final class PlayBillingPurchaseSnapshotMapper {
    private static final int PURCHASED = 1;
    private static final int PENDING = 2;

    private PlayBillingPurchaseSnapshotMapper() {
    }

    public static BillingPurchaseSnapshot fromPurchaseState(int purchaseState, boolean acknowledged) {
        if (purchaseState == PURCHASED) {
            return BillingPurchaseSnapshot.purchased(acknowledged);
        }
        if (purchaseState == PENDING) {
            return BillingPurchaseSnapshot.pending();
        }
        return BillingPurchaseSnapshot.notPurchased();
    }

    public static BillingPurchaseSnapshot notPurchased() {
        return BillingPurchaseSnapshot.notPurchased();
    }

    public static BillingPurchaseSnapshot billingUnavailable() {
        return BillingPurchaseSnapshot.billingUnavailable();
    }
}
