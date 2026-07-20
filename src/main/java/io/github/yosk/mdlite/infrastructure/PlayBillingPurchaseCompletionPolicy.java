package io.github.yosk.mdlite.infrastructure;

public final class PlayBillingPurchaseCompletionPolicy {
    private static final int BILLING_OK = 0;
    private static final int BILLING_USER_CANCELED = 1;
    private static final int PURCHASED = 1;
    private static final int PENDING = 2;

    private PlayBillingPurchaseCompletionPolicy() {
    }

    public static PlayBillingPurchaseCompletionAction from(
            int billingResponseCode,
            boolean targetProduct,
            int purchaseState,
            boolean acknowledged) {
        if (billingResponseCode != BILLING_OK || !targetProduct) {
            return PlayBillingPurchaseCompletionAction.ignore();
        }
        if (purchaseState == PURCHASED && !acknowledged) {
            return PlayBillingPurchaseCompletionAction.acknowledgeThenRefresh();
        }
        if (purchaseState == PURCHASED || purchaseState == PENDING) {
            return PlayBillingPurchaseCompletionAction.refresh();
        }
        return PlayBillingPurchaseCompletionAction.ignore();
    }

    public static PlayBillingPurchaseCompletionAction fromEmptyPurchaseUpdate(int billingResponseCode) {
        if (billingResponseCode == BILLING_USER_CANCELED) {
            return PlayBillingPurchaseCompletionAction.refresh();
        }
        return PlayBillingPurchaseCompletionAction.ignore();
    }
}
