package io.github.yosk.mdlite.domain;

public final class CodeHighlightingPolicyTest {
    public static void main(String[] args) {
        CodeHighlightingPolicyTest test = new CodeHighlightingPolicyTest();
        test.freeEntitlementKeepsCodeHighlightingPlain();
        test.proEntitlementEnablesSyntaxHighlightedCodeBlocks();
        test.missingEntitlementFallsBackToPlainCodeHighlighting();
    }

    public void freeEntitlementKeepsCodeHighlightingPlain() {
        CodeHighlighting highlighting = CodeHighlightingPolicy.fromEntitlement(FeatureEntitlement.free());

        assertFalse(highlighting.isEnabled(), "Free entitlement must keep code blocks plain");
    }

    public void proEntitlementEnablesSyntaxHighlightedCodeBlocks() {
        CodeHighlighting highlighting = CodeHighlightingPolicy.fromEntitlement(FeatureEntitlement.pro());

        assertTrue(highlighting.isEnabled(), "Pro entitlement must enable syntax-highlighted code blocks");
    }

    public void missingEntitlementFallsBackToPlainCodeHighlighting() {
        CodeHighlighting highlighting = CodeHighlightingPolicy.fromEntitlement(null);

        assertFalse(highlighting.isEnabled(), "Missing entitlement must keep the viewer usable as Free");
    }

    private static void assertTrue(boolean actual, String message) {
        if (!actual) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean actual, String message) {
        if (actual) {
            throw new AssertionError(message);
        }
    }
}
