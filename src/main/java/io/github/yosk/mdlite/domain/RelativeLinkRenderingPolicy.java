package io.github.yosk.mdlite.domain;

public final class RelativeLinkRenderingPolicy {
    private RelativeLinkRenderingPolicy() {
    }

    public static RelativeLinkRendering fromEntitlement(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (safeEntitlement.allows(ViewerFeature.RELATIVE_LINKS)) {
            return RelativeLinkRendering.enabled();
        }
        return RelativeLinkRendering.disabled();
    }
}
