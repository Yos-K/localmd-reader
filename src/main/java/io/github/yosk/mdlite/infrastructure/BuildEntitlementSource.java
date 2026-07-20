package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.EntitlementSource;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.StaticEntitlementSource;

public final class BuildEntitlementSource {
    private BuildEntitlementSource() {
    }

    public static EntitlementSource current() {
        if (BuildConfig.PRO_FEATURES_ENABLED) {
            return StaticEntitlementSource.pro();
        }
        return StaticEntitlementSource.free();
    }
}
