package io.github.yosk.mdlite.domain;

public final class ProPurchaseState {
    private static final int PURCHASED = 1;
    private static final int NOT_PURCHASED = 2;
    private static final int PENDING = 3;
    private static final int UNKNOWN = 4;
    private static final int BILLING_UNAVAILABLE = 5;
    private static final String PURCHASED_CODE = "purchased";
    private static final String NOT_PURCHASED_CODE = "not_purchased";
    private static final String PENDING_CODE = "pending";
    private static final String UNKNOWN_CODE = "unknown";
    private static final String BILLING_UNAVAILABLE_CODE = "billing_unavailable";

    private final int value;

    private ProPurchaseState(int value) {
        this.value = value;
    }

    public static ProPurchaseState purchased() {
        return new ProPurchaseState(PURCHASED);
    }

    public static ProPurchaseState notPurchased() {
        return new ProPurchaseState(NOT_PURCHASED);
    }

    public static ProPurchaseState pending() {
        return new ProPurchaseState(PENDING);
    }

    public static ProPurchaseState unknown() {
        return new ProPurchaseState(UNKNOWN);
    }

    public static ProPurchaseState billingUnavailable() {
        return new ProPurchaseState(BILLING_UNAVAILABLE);
    }

    public static ProPurchaseState fromPersistenceCode(String code) {
        if (PURCHASED_CODE.equals(code)) {
            return purchased();
        }
        if (NOT_PURCHASED_CODE.equals(code)) {
            return notPurchased();
        }
        if (PENDING_CODE.equals(code)) {
            return pending();
        }
        if (BILLING_UNAVAILABLE_CODE.equals(code)) {
            return billingUnavailable();
        }
        return unknown();
    }

    public FeatureEntitlement entitlement() {
        if (value == PURCHASED) {
            return FeatureEntitlement.pro();
        }
        return FeatureEntitlement.free();
    }

    boolean isPending() {
        return value == PENDING;
    }

    boolean isUnavailableOrUnknown() {
        return value == BILLING_UNAVAILABLE || value == UNKNOWN;
    }

    public String persistenceCode() {
        if (value == PURCHASED) {
            return PURCHASED_CODE;
        }
        if (value == NOT_PURCHASED) {
            return NOT_PURCHASED_CODE;
        }
        if (value == PENDING) {
            return PENDING_CODE;
        }
        if (value == BILLING_UNAVAILABLE) {
            return BILLING_UNAVAILABLE_CODE;
        }
        return UNKNOWN_CODE;
    }
}
