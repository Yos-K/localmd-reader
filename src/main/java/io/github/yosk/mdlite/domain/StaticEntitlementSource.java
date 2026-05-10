package io.github.yosk.mdlite.domain;

public final class StaticEntitlementSource implements EntitlementSource {
    private static final StaticEntitlementSource FREE = new StaticEntitlementSource(FeatureEntitlement.free());
    private static final StaticEntitlementSource PRO = new StaticEntitlementSource(FeatureEntitlement.pro());

    private final FeatureEntitlement entitlement;

    private StaticEntitlementSource(FeatureEntitlement entitlement) {
        this.entitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
    }

    public static StaticEntitlementSource free() {
        return FREE;
    }

    public static StaticEntitlementSource pro() {
        return PRO;
    }

    public static StaticEntitlementSource of(FeatureEntitlement entitlement) {
        return new StaticEntitlementSource(entitlement);
    }

    @Override
    public FeatureEntitlement currentEntitlement() {
        return entitlement;
    }
}
