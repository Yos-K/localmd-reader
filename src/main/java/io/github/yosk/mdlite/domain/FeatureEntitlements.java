package io.github.yosk.mdlite.domain;

public final class FeatureEntitlements {
    private FeatureEntitlements() {
    }

    public static FeatureEntitlement currentClosedTestingRelease() {
        return FeatureEntitlement.free();
    }
}
