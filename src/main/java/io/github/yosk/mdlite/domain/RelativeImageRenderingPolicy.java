package io.github.yosk.mdlite.domain;

public final class RelativeImageRenderingPolicy {
    private RelativeImageRenderingPolicy() {
    }

    public static RelativeImageRendering fromEntitlement(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (safeEntitlement.allows(ViewerFeature.RELATIVE_IMAGES)) {
            return RelativeImageRendering.enabled();
        }
        return RelativeImageRendering.disabled();
    }
}
