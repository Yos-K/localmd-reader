package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class BuildEntitlementSourceTest {

    @Test
    void currentBuildEntitlementReflectsProFeaturesBuildFlag() {
        // The same test sources run for both the Free flavor (PRO_FEATURES_ENABLED
        // = false) and the Pro preview flavor (= true) under Gradle, so the
        // expectation must track the build flag rather than assume Free.
        FeatureEntitlement entitlement = BuildEntitlementSource.current().currentEntitlement();

        TestAssertions.assertTrue(
                entitlement.isPro() == BuildConfig.PRO_FEATURES_ENABLED,
                "Build entitlement must reflect the PRO_FEATURES_ENABLED build flag");
    }
}
