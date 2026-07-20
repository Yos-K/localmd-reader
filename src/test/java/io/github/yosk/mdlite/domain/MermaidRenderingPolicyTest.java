package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MermaidRenderingPolicyTest {

    @Test
    void freeEntitlementEnablesMermaidDiagrams() {
        MermaidRendering rendering = MermaidRenderingPolicy.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertTrue(rendering.isEnabled(), "Free entitlement must render Mermaid diagrams");
    }

    @Test
    void proEntitlementEnablesMermaidDiagrams() {
        MermaidRendering rendering = MermaidRenderingPolicy.fromEntitlement(FeatureEntitlement.pro());

        TestAssertions.assertTrue(rendering.isEnabled(), "Pro entitlement must enable Mermaid diagram rendering");
    }

    @Test
    void missingEntitlementKeepsMermaidAsPlainCode() {
        MermaidRendering rendering = MermaidRenderingPolicy.fromEntitlement(null);

        TestAssertions.assertTrue(rendering.isEnabled(), "Missing entitlement must use the Free Mermaid rendering experience");
    }
}
