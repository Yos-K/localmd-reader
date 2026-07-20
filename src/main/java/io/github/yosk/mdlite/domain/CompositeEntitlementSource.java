package io.github.yosk.mdlite.domain;

public final class CompositeEntitlementSource implements EntitlementSource {
    private final EntitlementSource[] sources;

    private CompositeEntitlementSource(EntitlementSource[] sources) {
        this.sources = sources == null ? new EntitlementSource[0] : sources.clone();
    }

    public static CompositeEntitlementSource anyPro(EntitlementSource... sources) {
        return new CompositeEntitlementSource(sources);
    }

    @Override
    public FeatureEntitlement currentEntitlement() {
        for (int index = 0; index < sources.length; index++) {
            EntitlementSource source = sources[index];
            FeatureEntitlement entitlement = source == null ? FeatureEntitlement.free() : source.currentEntitlement();
            if (entitlement != null && entitlement.isPro()) {
                return FeatureEntitlement.pro();
            }
        }
        return FeatureEntitlement.free();
    }
}
