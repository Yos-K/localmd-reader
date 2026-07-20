package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public final class MarkdownHeadingsTest {

    @Test
    void extractsHeadingLevelsTitlesAndAnchorsFromMarkdown() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Title\n\n## Domain Model\nBody");

        TestAssertions.assertEquals(2, headings.count(), "Markdown headings must include h1 and h2 headings");
        TestAssertions.assertEquals(1, headings.at(0).level(), "first heading level");
        TestAssertions.assertEquals("Title", headings.at(0).title(), "first heading title");
        TestAssertions.assertEquals("title", headings.at(0).anchorId(), "first heading anchor");
        TestAssertions.assertEquals(2, headings.at(1).level(), "second heading level");
        TestAssertions.assertEquals("Domain Model", headings.at(1).title(), "second heading title");
        TestAssertions.assertEquals("domain-model", headings.at(1).anchorId(), "second heading anchor");
    }

    @Test
    void repeatedHeadingTitlesReceiveStableUniqueAnchors() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Intro\n## Intro\n### Intro");

        TestAssertions.assertEquals("intro", headings.at(0).anchorId(), "first repeated heading keeps the base anchor");
        TestAssertions.assertEquals("intro-2", headings.at(1).anchorId(), "second repeated heading receives numeric suffix");
        TestAssertions.assertEquals("intro-3", headings.at(2).anchorId(), "third repeated heading receives numeric suffix");
    }

    @Test
    void codeBlockHashesAreIgnored() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Real\n```md\n# Not heading\n```\n## Next");

        TestAssertions.assertEquals(2, headings.count(), "Markdown headings must ignore hash lines inside fenced code blocks");
        TestAssertions.assertEquals("Real", headings.at(0).title(), "first real heading");
        TestAssertions.assertEquals("Next", headings.at(1).title(), "second real heading");
    }

    @Test
    void blankMarkdownHasNoHeadings() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown(null);

        TestAssertions.assertEquals(0, headings.count(), "Blank Markdown must have no headings");
    }

    @Test
    void h6IsMaximumHeadingLevel() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("###### Level Six\n####### Not Heading");

        TestAssertions.assertEquals(1, headings.count(), "only levels 1-6 are valid headings");
        TestAssertions.assertEquals(6, headings.at(0).level(), "six hashes produce level 6 heading");
        TestAssertions.assertEquals("Level Six", headings.at(0).title(), "h6 title extracted correctly");
    }

    @Test
    void hashWithoutSpaceIsNotAHeading() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("#NoSpace\n# Valid");

        TestAssertions.assertEquals(1, headings.count(), "hash without trailing space must not be parsed as heading");
        TestAssertions.assertEquals("Valid", headings.at(0).title(), "line with space is the only heading");
    }

    @Test
    void headingWithOnlyWhitespaceAfterHashIsIgnored() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("#    \n# Real");

        TestAssertions.assertEquals(1, headings.count(), "heading with blank content after hash must be ignored");
        TestAssertions.assertEquals("Real", headings.at(0).title(), "non-blank heading is extracted");
    }

    @Test
    void fencedCodeBlockWithLanguageTagIgnoresHeadingsInside() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Before\n```java\n# Inside\n```\n# After");

        TestAssertions.assertEquals(2, headings.count(), "language-tagged fence block must suppress inner headings");
        TestAssertions.assertEquals("Before", headings.at(0).title(), "heading before fence block");
        TestAssertions.assertEquals("After", headings.at(1).title(), "heading after fence block");
    }

    @Test
    void itemsReturnsAllHeadingsAsList() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# A\n## B");

        List<MarkdownHeading> items = headings.items();

        TestAssertions.assertEquals(2, items.size(), "items() must return all headings");
        TestAssertions.assertEquals("A", items.get(0).title(), "first item title");
        TestAssertions.assertEquals("B", items.get(1).title(), "second item title");
    }

    @Test
    void fromMarkdownReturnsParsedHeadingsObject() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Only");

        TestAssertions.assertNotNull(headings, "fromMarkdown must return a non-null MarkdownHeadings");
        TestAssertions.assertEquals(1, headings.count(), "single heading document has count 1");
        TestAssertions.assertNotNull(headings.at(0), "at(0) must return the single heading");
    }

    @Test
    void windowsLineEndingsAreHandled() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# First\r\n## Second");

        TestAssertions.assertEquals(2, headings.count(), "CRLF line endings must be normalised");
        TestAssertions.assertEquals("First", headings.at(0).title(), "heading before CRLF");
        TestAssertions.assertEquals("Second", headings.at(1).title(), "heading after CRLF");
    }

    // Anchors only admit ASCII a-z/0-9/hyphen: a non-ASCII title has an empty base
    // and falls back to "heading", so several non-ASCII headings are numbered apart.
    // Titles stay intact; only the anchor collapses. (exploration 2026-06-13 P4)
    @Test
    void nonAsciiHeadingTitlesCollapseToTheHeadingAnchorAndAreNumbered() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# 概要\n# 詳細");

        TestAssertions.assertEquals("概要", headings.at(0).title(), "non-ASCII title is preserved as-is");
        TestAssertions.assertEquals("heading", headings.at(0).anchorId(), "first non-ASCII heading falls back to the heading anchor");
        TestAssertions.assertEquals("heading-2", headings.at(1).anchorId(), "second non-ASCII heading is numbered apart");
    }

    // Anchors are case-folded, so two titles differing only by case share a base
    // and the later one receives a numeric suffix to keep anchors unique (N1).
    // (exploration 2026-06-13 P10)
    @Test
    void titlesDifferingOnlyByCaseReceiveUniqueAnchors() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Foo\n# foo");

        TestAssertions.assertEquals("foo", headings.at(0).anchorId(), "first heading keeps the case-folded base anchor");
        TestAssertions.assertEquals("foo-2", headings.at(1).anchorId(), "case-only variant is numbered apart");
    }

    // CommonMark allows up to 3 spaces of indentation before a code fence; hash lines inside
    // such a fence are code, not headings. (issue #168, exploration 2026-06-13 P1)
    @Test
    void indentedCodeFenceSuppressesInnerHeadings() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Real\n   ```\n# Not heading\n   ```\n## After");

        TestAssertions.assertEquals(2, headings.count(), "up-to-3-space indented fences must suppress inner hash lines");
        TestAssertions.assertEquals("Real", headings.at(0).title(), "heading before the indented fence");
        TestAssertions.assertEquals("After", headings.at(1).title(), "heading after the indented fence");
    }

    // A closing fence may carry trailing whitespace; it must still close the block, otherwise
    // every later heading is swallowed and drops out of the table of contents. (issue #168, P2)
    @Test
    void closingFenceWithTrailingSpaceStillClosesTheBlock() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Real\n```\n# Not heading\n``` \n## After");

        TestAssertions.assertEquals(2, headings.count(), "a trailing-space closing fence must still close the code block");
        TestAssertions.assertEquals("Real", headings.at(0).title(), "heading before the fence");
        TestAssertions.assertEquals("After", headings.at(1).title(), "heading after the trailing-space close");
    }

    // 4+ spaces is an indented code block, not a fence: the backticks are literal, so a column-0
    // hash line that follows is still a heading. Pins the indentation boundary. (issue #168)
    @Test
    void fourSpaceIndentIsNotACodeFence() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Real\n    ```\n## After");

        TestAssertions.assertEquals(2, headings.count(), "4-space indent is not a fence, so the following heading still counts");
        TestAssertions.assertEquals("After", headings.at(1).title(), "heading after a 4-space-indented backtick line");
    }
}
