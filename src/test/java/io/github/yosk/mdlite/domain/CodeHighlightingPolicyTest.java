package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CodeHighlightingPolicyTest {

    @Test
    void freeEntitlementEnablesSyntaxHighlightedCodeBlocks() {
        CodeHighlighting highlighting = CodeHighlightingPolicy.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertTrue(highlighting.isEnabled(), "Free entitlement must enable syntax-highlighted code blocks");
    }

    @Test
    void proEntitlementEnablesSyntaxHighlightedCodeBlocks() {
        CodeHighlighting highlighting = CodeHighlightingPolicy.fromEntitlement(FeatureEntitlement.pro());

        TestAssertions.assertTrue(highlighting.isEnabled(), "Pro entitlement must enable syntax-highlighted code blocks");
    }

    @Test
    void missingEntitlementFallsBackToPlainCodeHighlighting() {
        CodeHighlighting highlighting = CodeHighlightingPolicy.fromEntitlement(null);

        TestAssertions.assertTrue(highlighting.isEnabled(), "Missing entitlement must use the Free code highlighting experience");
    }
}
