package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class DocumentRenderingProfileTest {

    @Test
    void freeProfileEnablesCodeHighlightingAvailableToEveryReader() {
        DocumentRenderingProfile profile = DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertTrue(profile.codeHighlighting().isEnabled(), "Free rendering must include code highlighting");
    }

    @Test
    void freeProfileEnablesMermaidRenderingAvailableToEveryReader() {
        DocumentRenderingProfile profile = DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertTrue(profile.mermaidRendering().isEnabled(), "Free rendering must include Mermaid diagrams");
    }

    @Test
    void freeProfileKeepsProRelativeLinksDisabled() {
        DocumentRenderingProfile profile = DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertFalse(profile.relativeLinkRendering().isEnabled(), "Free rendering must keep relative links disabled");
    }

    @Test
    void freeProfileKeepsProRelativeImagesDisabled() {
        DocumentRenderingProfile profile = DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertFalse(profile.relativeImageRendering().isEnabled(), "Free rendering must keep relative images disabled");
    }

    @Test
    void proProfileEnablesRelativeLinks() {
        DocumentRenderingProfile profile = DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.pro());

        TestAssertions.assertTrue(profile.relativeLinkRendering().isEnabled(), "Pro rendering must enable relative links");
    }

    @Test
    void proProfileEnablesRelativeImages() {
        DocumentRenderingProfile profile = DocumentRenderingProfile.fromEntitlement(FeatureEntitlement.pro());

        TestAssertions.assertTrue(profile.relativeImageRendering().isEnabled(), "Pro rendering must enable relative images");
    }

    @Test
    void missingEntitlementUsesTheSafeFreeProfile() {
        DocumentRenderingProfile profile = DocumentRenderingProfile.fromEntitlement(null);

        TestAssertions.assertFalse(profile.relativeLinkRendering().isEnabled(), "missing entitlement must not enable Pro rendering");
    }
}
