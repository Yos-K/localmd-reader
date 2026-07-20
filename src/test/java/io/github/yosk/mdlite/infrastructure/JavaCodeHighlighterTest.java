package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

/**
 * Example-based tests pinning the exact HTML {@link JavaCodeHighlighter} emits.
 *
 * Added to kill mutants that survived in the mutation run (a negated escape
 * branch, relaxed identifier char-range bounds, and the look-ahead in
 * nextNonSpaceCharacter that decides function calls). Each case asserts the full
 * string; identifiers run to end of line and use boundary characters so the
 * comparisons cannot be relaxed unnoticed.
 */
public final class JavaCodeHighlighterTest {

    @Test
    void keywordIsWrappedInKeywordSpan() {
        TestAssertions.assertEquals(
                "<span class=\"code-keyword\">return</span>",
                JavaCodeHighlighter.highlightLine("return"),
                "a Java keyword is wrapped in a keyword span");
    }

    @Test
    void literalIsWrappedInLiteralSpan() {
        TestAssertions.assertEquals(
                "<span class=\"code-literal\">true</span>",
                JavaCodeHighlighter.highlightLine("true"),
                "a Java literal is wrapped in a literal span");
    }

    @Test
    void classIntroducerHighlightsTheFollowingType() {
        TestAssertions.assertEquals(
                "<span class=\"code-keyword\">class</span> <span class=\"code-type\">Foo</span>",
                JavaCodeHighlighter.highlightLine("class Foo"),
                "the token after 'class' is highlighted as a type");
    }

    @Test
    void identifierFollowedByParenthesisIsAFunction() {
        TestAssertions.assertEquals(
                "<span class=\"code-function\">doStuff</span>()",
                JavaCodeHighlighter.highlightLine("doStuff()"),
                "an identifier immediately before '(' is highlighted as a function");
    }

    @Test
    void functionLookAheadSkipsSpacesBeforeParenthesis() {
        TestAssertions.assertEquals(
                "<span class=\"code-function\">doStuff</span> ()",
                JavaCodeHighlighter.highlightLine("doStuff ()"),
                "spaces before '(' are skipped when deciding a function call");
    }

    @Test
    void controlKeywordBeforeParenthesisStaysAKeyword() {
        TestAssertions.assertEquals(
                "<span class=\"code-keyword\">if</span> (",
                JavaCodeHighlighter.highlightLine("if ("),
                "a control keyword before '(' is a keyword, not a function");
    }

    @Test
    void specialCharactersOutsideTokensAreHtmlEscaped() {
        TestAssertions.assertEquals(
                "a &amp; b &lt; c &gt; &quot;d&quot;",
                JavaCodeHighlighter.highlightLine("a & b < c > \"d\""),
                "characters outside identifiers are HTML-escaped");
    }

    @Test
    void identifierStartBoundaryCharactersAreRecognised() {
        // Boundary letters used as function names: relaxing a start bound would
        // escape the letter instead of tokenising it, dropping the span.
        TestAssertions.assertEquals("<span class=\"code-keyword\">assert</span>",
                JavaCodeHighlighter.highlightLine("assert"), "'a' (lower bound) starts an identifier");
        TestAssertions.assertEquals("<span class=\"code-function\">zip</span>()",
                JavaCodeHighlighter.highlightLine("zip()"), "'z' (upper bound) starts an identifier");
        TestAssertions.assertEquals("<span class=\"code-function\">A</span>()",
                JavaCodeHighlighter.highlightLine("A()"), "'A' (lower bound) starts an identifier");
        TestAssertions.assertEquals("<span class=\"code-function\">Z</span>()",
                JavaCodeHighlighter.highlightLine("Z()"), "'Z' (upper bound) starts an identifier");
    }

    @Test
    void identifierPartDigitBoundariesAreRecognised() {
        TestAssertions.assertEquals("<span class=\"code-function\">x0</span>()",
                JavaCodeHighlighter.highlightLine("x0()"), "'0' (lower bound) is an identifier part");
        TestAssertions.assertEquals("<span class=\"code-function\">x9</span>()",
                JavaCodeHighlighter.highlightLine("x9()"), "'9' (upper bound) is an identifier part");
    }

    @Test
    void trailingIdentifierAtEndOfLineIsEmittedPlain() {
        TestAssertions.assertEquals(
                "name",
                JavaCodeHighlighter.highlightLine("name"),
                "an identifier that reaches the end of the line is emitted without overrunning the look-ahead");
    }

    @Test
    void nullLineProducesEmptyOutput() {
        TestAssertions.assertEquals("", JavaCodeHighlighter.highlightLine(null), "a null line is empty");
    }
}
