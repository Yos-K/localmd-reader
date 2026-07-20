package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class RelativeLinkRenderingPolicyTest {

    @Test
    void freeEntitlementKeepsRelativeLinksAsText() {
        RelativeLinkRendering rendering = RelativeLinkRenderingPolicy.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertFalse(rendering.isEnabled(), "Free entitlement must not render relative links as anchors");
    }

    @Test
    void proEntitlementEnablesRelativeLinks() {
        RelativeLinkRendering rendering = RelativeLinkRenderingPolicy.fromEntitlement(FeatureEntitlement.pro());

        TestAssertions.assertTrue(rendering.isEnabled(), "Pro entitlement must render safe relative links as anchors");
    }

    @Test
    void missingEntitlementKeepsRelativeLinksAsText() {
        RelativeLinkRendering rendering = RelativeLinkRenderingPolicy.fromEntitlement(null);

        TestAssertions.assertFalse(rendering.isEnabled(), "Missing entitlement must not render relative links as anchors");
    }
}
