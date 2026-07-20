package io.github.yosk.mdlite.domain;

public final class FeatureEntitlements {
    private FeatureEntitlements() {
    }

    public static FeatureEntitlement currentClosedTestingRelease() {
        return current(StaticEntitlementSource.free());
    }

    public static FeatureEntitlement current(EntitlementSource source) {
        EntitlementSource safeSource = source == null ? StaticEntitlementSource.free() : source;
        FeatureEntitlement entitlement = safeSource.currentEntitlement();
        return entitlement == null ? FeatureEntitlement.free() : entitlement;
    }
}
