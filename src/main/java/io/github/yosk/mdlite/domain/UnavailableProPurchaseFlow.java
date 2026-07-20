package io.github.yosk.mdlite.domain;

public final class UnavailableProPurchaseFlow implements ProPurchaseFlow {
    private static final UnavailableProPurchaseFlow INSTANCE = new UnavailableProPurchaseFlow();

    private UnavailableProPurchaseFlow() {
    }

    public static UnavailableProPurchaseFlow instance() {
        return INSTANCE;
    }

    @Override
    public ProPurchaseStartResult start(ProProduct product, ProPurchaseFlowCallback callback) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null.");
        }
        if (callback == null) {
            throw new IllegalArgumentException("Purchase flow callback must not be null.");
        }
        return ProPurchaseStartResult.unavailable();
    }
}
