package io.github.yosk.mdlite.domain;

public final class FeatureEntitlement {
    private static final FeatureEntitlement FREE = new FeatureEntitlement(false);
    private static final FeatureEntitlement PRO = new FeatureEntitlement(true);

    private final boolean pro;

    private FeatureEntitlement(boolean pro) {
        this.pro = pro;
    }

    public static FeatureEntitlement free() {
        return FREE;
    }

    public static FeatureEntitlement pro() {
        return PRO;
    }

    public boolean isPro() {
        return pro;
    }

    public boolean allows(ViewerFeature feature) {
        if (feature == null) {
            throw new IllegalArgumentException("Feature must not be null.");
        }
        return pro || feature.isFree();
    }
}
