package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class CodeHighlightingPolicyTest {
    public static void main(String[] args) {
        CodeHighlightingPolicyTest test = new CodeHighlightingPolicyTest();
        test.freeEntitlementKeepsCodeHighlightingPlain();
        test.proEntitlementEnablesSyntaxHighlightedCodeBlocks();
        test.missingEntitlementFallsBackToPlainCodeHighlighting();
    }

    public void freeEntitlementKeepsCodeHighlightingPlain() {
        CodeHighlighting highlighting = CodeHighlightingPolicy.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertFalse(highlighting.isEnabled(), "Free entitlement must keep code blocks plain");
    }

    public void proEntitlementEnablesSyntaxHighlightedCodeBlocks() {
        CodeHighlighting highlighting = CodeHighlightingPolicy.fromEntitlement(FeatureEntitlement.pro());

        TestAssertions.assertTrue(highlighting.isEnabled(), "Pro entitlement must enable syntax-highlighted code blocks");
    }

    public void missingEntitlementFallsBackToPlainCodeHighlighting() {
        CodeHighlighting highlighting = CodeHighlightingPolicy.fromEntitlement(null);

        TestAssertions.assertFalse(highlighting.isEnabled(), "Missing entitlement must keep the viewer usable as Free");
    }
}
