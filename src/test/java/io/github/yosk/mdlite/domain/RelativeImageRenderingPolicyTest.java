package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class RelativeImageRenderingPolicyTest {

    @Test
    void freeEntitlementMustNotRenderRelativeImagesAsImageTags() {
        RelativeImageRendering rendering = RelativeImageRenderingPolicy.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertFalse(rendering.isEnabled(), "Free entitlement must keep relative image Markdown as readable text");
    }

    @Test
    void proEntitlementMustRenderSafeRelativeImagesAsImageTags() {
        RelativeImageRendering rendering = RelativeImageRenderingPolicy.fromEntitlement(FeatureEntitlement.pro());

        TestAssertions.assertTrue(rendering.isEnabled(), "Pro entitlement must allow safe relative image Markdown to become image tags");
    }

    @Test
    void missingEntitlementMustFallBackToDisabledRelativeImages() {
        RelativeImageRendering rendering = RelativeImageRenderingPolicy.fromEntitlement(null);

        TestAssertions.assertFalse(rendering.isEnabled(), "Missing entitlement must fail safe and keep relative image rendering disabled");
    }
}
