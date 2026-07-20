package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class TableOfContentsItemsTest {
    public static void main(String[] args) {
        labelsPreserveHeadingHierarchyForFastInlineSelection();
        emptyHeadingsCreateEmptyTableOfContentsItems();
        itemKeepsHeadingJumpTarget();
    }

    private static void labelsPreserveHeadingHierarchyForFastInlineSelection() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Title\n\n## Domain\n\n### Details");

        TableOfContentsItems items = TableOfContentsItems.from(headings);

        TestAssertions.assertEquals(3, items.count(), "table of contents must expose every heading");
        TestAssertions.assertEquals("Title", items.at(0).label(), "level 1 heading must not be indented");
        TestAssertions.assertEquals("  Domain", items.at(1).label(), "level 2 heading must be visually nested");
        TestAssertions.assertEquals("    Details", items.at(2).label(), "level 3 heading must be visually nested deeper");
    }

    private static void emptyHeadingsCreateEmptyTableOfContentsItems() {
        TableOfContentsItems items = TableOfContentsItems.from(MarkdownHeadings.fromMarkdown(""));

        TestAssertions.assertEquals(0, items.count(), "document without headings must have no selectable table of contents items");
    }

    private static void itemKeepsHeadingJumpTarget() {
        MarkdownHeadings headings = MarkdownHeadings.fromMarkdown("# Intro");

        TableOfContentsItems items = TableOfContentsItems.from(headings);

        TestAssertions.assertEquals("intro", items.at(0).heading().anchorId(), "table of contents item must keep the heading jump target");
    }
}
