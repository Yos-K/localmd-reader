package io.github.yosk.mdlite.domain;

public final class CodeHighlightingPolicy {
    private CodeHighlightingPolicy() {
    }

    public static CodeHighlighting fromEntitlement(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (safeEntitlement.allows(ViewerFeature.CODE_HIGHLIGHTING)) {
            return CodeHighlighting.syntaxHighlighted();
        }
        return CodeHighlighting.plain();
    }
}
