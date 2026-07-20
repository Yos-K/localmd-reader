package io.github.yosk.mdlite.domain;

public final class MermaidRenderingPolicy {
    private MermaidRenderingPolicy() {
    }

    public static MermaidRendering fromEntitlement(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (safeEntitlement.allows(ViewerFeature.MERMAID_RENDERING)) {
            return MermaidRendering.diagrams();
        }
        return MermaidRendering.plainCode();
    }
}
