package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.domain.MermaidDiagramBlock;
import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MermaidRenderErrorHtmlTest {

    @Test
    void errorHtmlShowsReadableReasonAndOriginalSource() {
        SafeHtml html = MermaidRenderErrorHtml.from(
                MermaidDiagramBlock.fromSource("graph TD\nA-->B"),
                "Parse error");

        TestAssertions.assertContains(html.value(), "Unable to render this Mermaid diagram.", "Mermaid error must explain that rendering failed");
        TestAssertions.assertContains(html.value(), "Parse error", "Mermaid error must show the renderer reason");
        TestAssertions.assertContains(html.value(), "graph TD\nA--&gt;B", "Mermaid error must keep the original source readable");
    }

    @Test
    void errorHtmlEscapesReasonAndOriginalSource() {
        SafeHtml html = MermaidRenderErrorHtml.from(
                MermaidDiagramBlock.fromSource("graph TD\nA[<script>]"),
                "<b>bad</b>");

        TestAssertions.assertContains(html.value(), "&lt;b&gt;bad&lt;/b&gt;", "Mermaid error reason must be escaped");
        TestAssertions.assertContains(html.value(), "A[&lt;script&gt;]", "Mermaid source fallback must be escaped");
        TestAssertions.assertNotContains(html.value(), "<script>", "Mermaid source fallback must not emit raw script tags");
    }

    @Test
    void errorHtmlUsesFallbackReasonWhenRendererReasonIsMissing() {
        SafeHtml html = MermaidRenderErrorHtml.from(
                MermaidDiagramBlock.fromSource("graph TD\nA-->B"),
                "   ");

        TestAssertions.assertContains(html.value(), "The diagram syntax is not supported or contains an error.", "Missing Mermaid error reason must use a readable fallback");
    }
}
