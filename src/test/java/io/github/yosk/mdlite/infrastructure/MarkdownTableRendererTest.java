package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.RelativeImageRendering;
import io.github.yosk.mdlite.domain.RelativeLinkRendering;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownTableRendererTest {
    @Test
    void rowRequiresAtLeastTwoPipeSeparatedCells() {
        TestAssertions.assertFalse(MarkdownTableRenderer.isRow("one cell"),
                "plain text must not be mistaken for a table row");
    }

    @Test
    void alignedSeparatorAcceptsThreeOrMoreDashesPerCell() {
        TestAssertions.assertTrue(MarkdownTableRenderer.isSeparator("| :--- | ---: |"),
                "alignment markers around a valid separator must be accepted");
    }

    @Test
    void separatorRejectsACellWithOnlyTwoDashes() {
        TestAssertions.assertFalse(MarkdownTableRenderer.isSeparator("| -- | --- |"),
                "each separator cell must contain at least three dashes");
    }

    @Test
    void cellsAreTrimmedAndRenderedWithInlineMarkdown() {
        String rendered = MarkdownTableRenderer.renderCells(
                "| **Name** | Value |",
                "th",
                RelativeLinkRendering.disabled(),
                RelativeImageRendering.disabled());

        TestAssertions.assertEquals("<th><strong>Name</strong></th><th>Value</th>", rendered,
                "table cells must share the document inline grammar");
    }
}
