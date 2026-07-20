package io.github.yosk.mdlite.domain;

public final class ProPurchaseCacheEntry {
    private final ProPurchaseState purchaseState;
    private final long verifiedAtMillis;

    private ProPurchaseCacheEntry(ProPurchaseState purchaseState, long verifiedAtMillis) {
        this.purchaseState = purchaseState;
        this.verifiedAtMillis = verifiedAtMillis;
    }

    public static ProPurchaseCacheEntry verifiedAt(ProPurchaseState purchaseState, long verifiedAtMillis) {
        if (purchaseState == null) {
            throw new IllegalArgumentException("Purchase state must not be null.");
        }
        if (verifiedAtMillis < 0L) {
            throw new IllegalArgumentException("Verification time must not be negative.");
        }
        return new ProPurchaseCacheEntry(purchaseState, verifiedAtMillis);
    }

    public static ProPurchaseCacheEntry restore(String purchaseStateCode, long verifiedAtMillis) {
        return verifiedAt(ProPurchaseState.fromPersistenceCode(purchaseStateCode), verifiedAtMillis);
    }

    public ProPurchaseState purchaseState() {
        return purchaseState;
    }

    public String purchaseStateCode() {
        return purchaseState.persistenceCode();
    }

    public long verifiedAtMillis() {
        return verifiedAtMillis;
    }
}
