package io.github.yosk.mdlite.infrastructure;

public final class BuildProPurchaseStatusProvider {
    private BuildProPurchaseStatusProvider() {
    }

    public static ProPurchaseStatusProvider current() {
        return UnavailableProPurchaseStatusProvider.instance();
    }

    public static ProPurchaseStatusProvider current(ProPurchaseStatusProvider billingProvider) {
        if (billingProvider == null) {
            throw new IllegalArgumentException("Billing purchase status provider must not be null.");
        }
        if (BuildConfig.PLAY_BILLING_ENABLED) {
            return billingProvider;
        }
        return UnavailableProPurchaseStatusProvider.instance();
    }
}
