package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

/**
 * Example-based tests pinning the exact HTML {@link MarkupCodeHighlighter} emits.
 *
 * Added to kill mutants that survived in the mutation run: the renderer covered
 * this class but nothing asserted its output. Each case asserts the full string,
 * so dropped escape calls, off-by-one tag indices, and flipped name-range bounds
 * all change the output. Boundary characters ('a'/'z'/'A'/'Z'/'0'/'9') are used
 * deliberately so the discrete char-range comparisons cannot be relaxed unnoticed.
 */
public final class MarkupCodeHighlighterTest {

    @Test
    void fullTagHighlightsTypeAttributeAndQuotedValue() {
        TestAssertions.assertEquals(
                "&lt;<span class=\"code-type\">a</span> "
                        + "<span class=\"code-variable\">href</span>="
                        + "<span class=\"code-string\">&quot;x&quot;</span>&gt;",
                MarkupCodeHighlighter.highlightLine("<a href=\"x\">"),
                "an opening tag highlights the element name, attribute, and quoted value");
    }

    @Test
    void closingTagKeepsSlashAndHighlightsType() {
        TestAssertions.assertEquals(
                "&lt;/<span class=\"code-type\">p</span>&gt;",
                MarkupCodeHighlighter.highlightLine("</p>"),
                "a closing tag keeps its slash and highlights the element name");
    }

    @Test
    void textOutsideTagsIsHtmlEscaped() {
        TestAssertions.assertEquals(
                "a &amp; b &gt; c",
                MarkupCodeHighlighter.highlightLine("a & b > c"),
                "text outside tags is HTML-escaped character by character");
    }

    @Test
    void tagRunningToEndOfLineHighlightsTheName() {
        TestAssertions.assertEquals(
                "&lt;<span class=\"code-type\">section</span>",
                MarkupCodeHighlighter.highlightLine("<section"),
                "a tag that reaches the end of the line still highlights the name without overrunning");
    }

    @Test
    void unterminatedQuotedAttributeIsClosedAtEndOfLine() {
        TestAssertions.assertEquals(
                "&lt;<span class=\"code-type\">a</span> "
                        + "<span class=\"code-variable\">b</span>="
                        + "<span class=\"code-string\">'x</span>",
                MarkupCodeHighlighter.highlightLine("<a b='x"),
                "an unterminated quoted value is closed at end of line without overrunning");
    }

    @Test
    void tagNamesAcceptAsciiLetterUnderscoreAndColonBoundaries() {
        // Boundary letters of each range plus '_' and ':' must all start a name.
        TestAssertions.assertEquals("&lt;<span class=\"code-type\">a</span>&gt;",
                MarkupCodeHighlighter.highlightLine("<a>"), "'a' (lower bound) starts a name");
        TestAssertions.assertEquals("&lt;<span class=\"code-type\">z</span>&gt;",
                MarkupCodeHighlighter.highlightLine("<z>"), "'z' (upper bound) starts a name");
        TestAssertions.assertEquals("&lt;<span class=\"code-type\">A</span>&gt;",
                MarkupCodeHighlighter.highlightLine("<A>"), "'A' (lower bound) starts a name");
        TestAssertions.assertEquals("&lt;<span class=\"code-type\">Z</span>&gt;",
                MarkupCodeHighlighter.highlightLine("<Z>"), "'Z' (upper bound) starts a name");
        TestAssertions.assertEquals("&lt;<span class=\"code-type\">_x</span>&gt;",
                MarkupCodeHighlighter.highlightLine("<_x>"), "'_' starts a name");
        TestAssertions.assertEquals("&lt;<span class=\"code-type\">a:b</span>&gt;",
                MarkupCodeHighlighter.highlightLine("<a:b>"), "':' is part of a name");
    }

    @Test
    void nameCharactersAcceptDigitBoundaries() {
        // '0' and '9' (the digit range bounds) must be accepted as name parts.
        TestAssertions.assertEquals("&lt;<span class=\"code-type\">h0</span>&gt;",
                MarkupCodeHighlighter.highlightLine("<h0>"), "'0' (lower bound) is a name part");
        TestAssertions.assertEquals("&lt;<span class=\"code-type\">h9</span>&gt;",
                MarkupCodeHighlighter.highlightLine("<h9>"), "'9' (upper bound) is a name part");
    }

    @Test
    void nullLineProducesEmptyOutput() {
        TestAssertions.assertEquals("", MarkupCodeHighlighter.highlightLine(null), "a null line is empty");
    }
}
