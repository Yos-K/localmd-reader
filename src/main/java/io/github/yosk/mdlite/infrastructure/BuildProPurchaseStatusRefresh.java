package io.github.yosk.mdlite.infrastructure;

public final class BuildProPurchaseStatusRefresh {
    private BuildProPurchaseStatusRefresh() {
    }

    public static ProPurchaseStatusRefresh current(ProPurchaseCacheStore store) {
        return current(store, BuildProPurchaseStatusProvider.current());
    }

    public static ProPurchaseStatusRefresh current(ProPurchaseCacheStore store, ProPurchaseStatusProvider provider) {
        return new ProPurchaseStatusRefresh(
                new ProPurchaseCacheRefresh(store),
                provider);
    }
}
