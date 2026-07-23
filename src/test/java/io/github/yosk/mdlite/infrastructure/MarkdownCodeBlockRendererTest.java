package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.CodeHighlighting;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public final class MarkdownCodeBlockRendererTest {
    @Test
    void plainCodeBlockEscapesMarkupAndClosesTheCodeElement() {
        String rendered = MarkdownCodeBlockRenderer.renderLines(
                "<tag>\n", CodeFenceInfo.fromFenceLine("```html"), CodeHighlighting.plain());

        TestAssertions.assertEquals("&lt;tag&gt;\n</code></pre>", rendered,
                "plain code must remain inert text inside a complete code block");
    }

    @Test
    void fenceIndentedByThreeSpacesIsRecognized() {
        TestAssertions.assertTrue(MarkdownCodeBlockRenderer.isFenceLine("   ```java"),
                "CommonMark permits a code fence indented by up to three spaces");
    }

    @Test
    void fenceIndentedByFourSpacesIsNotRecognized() {
        TestAssertions.assertFalse(MarkdownCodeBlockRenderer.isFenceLine("    ```java"),
                "four-space indentation belongs to indented code rather than a fence");
    }

    @Test
    void pendingMermaidDiagramEscapesItsSourceInThePlaceholder() {
        String rendered = MarkdownCodeBlockRenderer.renderMermaid(
                2, "graph TD\nA[<unsafe>]", new HashMap<Integer, SafeHtml>());

        TestAssertions.assertContains(rendered, "A[&lt;unsafe&gt;]",
                "a pending Mermaid source must not inject markup into the reader");
    }

    @Test
    void completedMermaidDiagramReplacesThePendingPlaceholder() {
        Map<Integer, SafeHtml> diagrams = new HashMap<Integer, SafeHtml>();
        diagrams.put(Integer.valueOf(2), SafeHtml.fromTrustedRendererOutput("<svg>ready</svg>"));

        String rendered = MarkdownCodeBlockRenderer.renderMermaid(2, "graph TD", diagrams);

        TestAssertions.assertEquals(
                "<div class=\"mermaid-diagram\"><div class=\"mermaid-diagram-scale\"><svg>ready</svg></div></div>",
                rendered,
                "a completed background render must replace its matching placeholder");
    }
}
