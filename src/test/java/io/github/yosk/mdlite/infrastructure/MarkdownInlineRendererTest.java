package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.RelativeImageRendering;
import io.github.yosk.mdlite.domain.RelativeLinkRendering;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownInlineRendererTest {
    @Test
    void strongContentRetainsNestedInlineCodeRendering() {
        String rendered = MarkdownInlineRenderer.render(
                "**use `code`**", RelativeLinkRendering.disabled(), RelativeImageRendering.disabled());

        TestAssertions.assertEquals("<strong>use <code>code</code></strong>", rendered,
                "strong content must use the same inline grammar recursively");
    }

    @Test
    void inlineCodeEscapesHtmlInsteadOfExecutingIt() {
        String rendered = MarkdownInlineRenderer.render(
                "`<script>`", RelativeLinkRendering.disabled(), RelativeImageRendering.disabled());

        TestAssertions.assertEquals("<code>&lt;script&gt;</code>", rendered,
                "inline code must remain inert text");
    }

    @Test
    void javascriptLinkRendersOnlyItsReadableLabel() {
        String rendered = MarkdownInlineRenderer.render(
                "[open](javascript:alert(1))",
                RelativeLinkRendering.enabled(), RelativeImageRendering.disabled());

        TestAssertions.assertEquals("open)", rendered,
                "an unsafe link target must never become an anchor");
    }

    @Test
    void enabledRelativeLinkUsesTheLocalDocumentRequestBoundary() {
        String rendered = MarkdownInlineRenderer.render(
                "[next](notes/next file.md)",
                RelativeLinkRendering.enabled(), RelativeImageRendering.disabled());

        TestAssertions.assertEquals(
                "<a href=\"https://localmd.local/__relative_markdown__?path=notes%2Fnext+file.md\">next</a>",
                rendered,
                "relative Markdown links must pass through the local request adapter");
    }

    @Test
    void disabledRelativeImageRendersAccessibleAlternativeText() {
        String rendered = MarkdownInlineRenderer.render(
                "![diagram](images/flow.png)",
                RelativeLinkRendering.disabled(), RelativeImageRendering.disabled());

        TestAssertions.assertEquals("diagram", rendered,
                "disabled relative images must preserve their alternative text");
    }
}
