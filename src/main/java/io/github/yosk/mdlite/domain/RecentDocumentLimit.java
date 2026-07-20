package io.github.yosk.mdlite.domain;

public final class RecentDocumentLimit {
    private static final int FREE_MAX_ITEMS = 5;
    private static final int PRO_MAX_ITEMS = 20;

    private final int maxItems;

    private RecentDocumentLimit(int maxItems) {
        this.maxItems = maxItems;
    }

    public static RecentDocumentLimit fromEntitlement(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        return new RecentDocumentLimit(
                safeEntitlement.allows(ViewerFeature.EXTENDED_RECENT_FILES) ? PRO_MAX_ITEMS : FREE_MAX_ITEMS);
    }

    public int maxItems() {
        return maxItems;
    }
}
