package io.github.yosk.mdlite.domain;

public final class BillingPurchaseSnapshot {
    private static final int PURCHASED = 1;
    private static final int PENDING = 2;
    private static final int NOT_PURCHASED = 3;
    private static final int BILLING_UNAVAILABLE = 4;

    private final int state;
    private final boolean acknowledged;

    private BillingPurchaseSnapshot(int state, boolean acknowledged) {
        this.state = state;
        this.acknowledged = acknowledged;
    }

    public static BillingPurchaseSnapshot purchased(boolean acknowledged) {
        return new BillingPurchaseSnapshot(PURCHASED, acknowledged);
    }

    public static BillingPurchaseSnapshot pending() {
        return new BillingPurchaseSnapshot(PENDING, false);
    }

    public static BillingPurchaseSnapshot notPurchased() {
        return new BillingPurchaseSnapshot(NOT_PURCHASED, false);
    }

    public static BillingPurchaseSnapshot billingUnavailable() {
        return new BillingPurchaseSnapshot(BILLING_UNAVAILABLE, false);
    }

    public ProPurchaseState proPurchaseState() {
        if (state == PURCHASED && acknowledged) {
            return ProPurchaseState.purchased();
        }
        if (state == PURCHASED || state == PENDING) {
            return ProPurchaseState.pending();
        }
        if (state == BILLING_UNAVAILABLE) {
            return ProPurchaseState.billingUnavailable();
        }
        return ProPurchaseState.notPurchased();
    }
}
