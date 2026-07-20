package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

/**
 * Example-based tests pinning the exact HTML {@link KeywordCodeHighlighter} emits.
 *
 * Added to kill mutants that survived in the mutation run (escape branches were
 * negated, the top-level escape call was dropped, and the identifier char-range
 * bounds were relaxed). Each case asserts the full string; boundary characters
 * ('a'/'z'/'A'/'Z'/'_'/'$'/'0'/'9') are wrapped as keyword tokens so a relaxed
 * range bound stops recognising them and changes the output.
 */
public final class KeywordCodeHighlighterTest {

    private static final String[] NONE = new String[0];

    @Test
    void keywordTokenIsWrappedInKeywordSpan() {
        TestAssertions.assertEquals(
                "<span class=\"code-keyword\">if</span>",
                KeywordCodeHighlighter.highlightLine("if", new String[] {"if"}, NONE),
                "a token in the keyword list is wrapped in a keyword span");
    }

    @Test
    void literalTokenIsWrappedInLiteralSpan() {
        TestAssertions.assertEquals(
                "<span class=\"code-literal\">true</span>",
                KeywordCodeHighlighter.highlightLine("true", NONE, new String[] {"true"}),
                "a token in the literal list is wrapped in a literal span");
    }

    @Test
    void plainIdentifierIsEmittedWithoutASpan() {
        TestAssertions.assertEquals(
                "foo",
                KeywordCodeHighlighter.highlightLine("foo", new String[] {"if"}, NONE),
                "an identifier that is neither keyword nor literal is emitted plain");
    }

    @Test
    void specialCharactersBetweenTokensAreHtmlEscaped() {
        TestAssertions.assertEquals(
                "a &amp; b &lt; c &gt; d &quot; e",
                KeywordCodeHighlighter.highlightLine("a & b < c > d \" e", NONE, NONE),
                "characters outside identifiers are escaped (each escape branch is exercised)");
    }

    @Test
    void identifierStartBoundaryCharactersAreRecognisedAsTokens() {
        // Each is a one-character keyword: if the start-range bound were relaxed the
        // character would be escaped instead of tokenised, dropping the span.
        TestAssertions.assertEquals("<span class=\"code-keyword\">a</span>",
                KeywordCodeHighlighter.highlightLine("a", new String[] {"a"}, NONE), "'a' starts an identifier");
        TestAssertions.assertEquals("<span class=\"code-keyword\">z</span>",
                KeywordCodeHighlighter.highlightLine("z", new String[] {"z"}, NONE), "'z' starts an identifier");
        TestAssertions.assertEquals("<span class=\"code-keyword\">A</span>",
                KeywordCodeHighlighter.highlightLine("A", new String[] {"A"}, NONE), "'A' starts an identifier");
        TestAssertions.assertEquals("<span class=\"code-keyword\">Z</span>",
                KeywordCodeHighlighter.highlightLine("Z", new String[] {"Z"}, NONE), "'Z' starts an identifier");
        TestAssertions.assertEquals("<span class=\"code-keyword\">_</span>",
                KeywordCodeHighlighter.highlightLine("_", new String[] {"_"}, NONE), "'_' starts an identifier");
        TestAssertions.assertEquals("<span class=\"code-keyword\">$</span>",
                KeywordCodeHighlighter.highlightLine("$", new String[] {"$"}, NONE), "'$' starts an identifier");
    }

    @Test
    void identifierPartDigitBoundariesAreRecognised() {
        TestAssertions.assertEquals("<span class=\"code-keyword\">a0</span>",
                KeywordCodeHighlighter.highlightLine("a0", new String[] {"a0"}, NONE), "'0' is an identifier part");
        TestAssertions.assertEquals("<span class=\"code-keyword\">a9</span>",
                KeywordCodeHighlighter.highlightLine("a9", new String[] {"a9"}, NONE), "'9' is an identifier part");
    }

    @Test
    void nullLineProducesEmptyOutput() {
        TestAssertions.assertEquals("", KeywordCodeHighlighter.highlightLine(null, NONE, NONE), "a null line is empty");
    }
}
