package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

/**
 * Example-based tests that pin the exact HTML {@link CssCodeHighlighter} emits.
 *
 * Added to kill mutants that survived in the mutation run: the renderer covered
 * this class but nothing asserted its output, so boundary flips, negated
 * conditionals, dropped escape calls, and replaced return values all survived.
 * Each case asserts the full output string, so any such mutation changes it.
 */
public final class CssCodeHighlighterTest {

    @Test
    void propertyLineWrapsNameInVariableSpanAndKeepsLeadingIndent() {
        TestAssertions.assertEquals(
                "  <span class=\"code-variable\">color</span>: red",
                CssCodeHighlighter.highlightLine("  color: red"),
                "a property line keeps its indent and wraps the property name in a variable span");
    }

    @Test
    void propertyLineWithoutIndentHasNoLeadingSpace() {
        TestAssertions.assertEquals(
                "<span class=\"code-variable\">margin</span>:0",
                CssCodeHighlighter.highlightLine("margin:0"),
                "a property line without indent must not gain a leading space");
    }

    @Test
    void propertyValueIsHtmlEscaped() {
        TestAssertions.assertEquals(
                "<span class=\"code-variable\">color</span>: a&lt;b",
                CssCodeHighlighter.highlightLine("color: a<b"),
                "the text after the colon must be HTML-escaped");
    }

    @Test
    void selectorWithBraceWrapsSelectorInTypeSpanAndKeepsSpacing() {
        TestAssertions.assertEquals(
                "  <span class=\"code-type\">.foo</span> {",
                CssCodeHighlighter.highlightLine("  .foo {"),
                "a selector line wraps the selector in a type span and keeps the trailing space and brace");
    }

    @Test
    void selectorFragmentWithoutBraceIsEscapedWithoutSpan() {
        TestAssertions.assertEquals(
                ".bar,",
                CssCodeHighlighter.highlightLine(".bar,"),
                "a selector fragment without a brace is escaped and gets no span");
    }

    @Test
    void lineStartingWithColonIsTreatedAsSelectorNotProperty() {
        TestAssertions.assertEquals(
                ":root",
                CssCodeHighlighter.highlightLine(":root"),
                "a colon at index 0 must not be treated as a property separator");
    }

    @Test
    void nullLineProducesEmptyOutput() {
        TestAssertions.assertEquals(
                "",
                CssCodeHighlighter.highlightLine(null),
                "a null line is treated as empty");
    }
}
