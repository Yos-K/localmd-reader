package io.github.yosk.mdlite.domain;

public final class ProPurchaseEntitlementSource implements EntitlementSource {
    private final ProPurchaseState purchaseState;

    private ProPurchaseEntitlementSource(ProPurchaseState purchaseState) {
        if (purchaseState == null) {
            throw new IllegalArgumentException("Purchase state must not be null.");
        }
        this.purchaseState = purchaseState;
    }

    public static ProPurchaseEntitlementSource from(ProPurchaseState purchaseState) {
        return new ProPurchaseEntitlementSource(purchaseState);
    }

    @Override
    public FeatureEntitlement currentEntitlement() {
        return purchaseState.entitlement();
    }
}
