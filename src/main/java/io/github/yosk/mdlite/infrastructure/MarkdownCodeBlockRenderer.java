package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.CodeHighlighting;
import io.github.yosk.mdlite.domain.MermaidRendering;
import io.github.yosk.mdlite.domain.RelativeImageRendering;
import io.github.yosk.mdlite.domain.RelativeLinkRendering;
import io.github.yosk.mdlite.domain.SafeHtml;
import java.util.Map;

final class MarkdownCodeBlockRenderer {
    private MarkdownCodeBlockRenderer() {
    }

    static String renderPreviewable(
            int index,
            String codeBlock,
            CodeFenceInfo codeFence,
            CodeHighlighting codeHighlighting) {
        SafeHtml raw = SafeHtml.fromTrustedRendererOutput(
                openingHtml(codeFence) + renderLines(codeBlock, codeFence, codeHighlighting));
        return CodeBlockPreviewHtml.from(index, raw, renderPreview(codeBlock, codeFence)).value();
    }

    static String renderLines(
            String codeBlock,
            CodeFenceInfo codeFence,
            CodeHighlighting codeHighlighting) {
        String[] lines = codeBlock.split("\\n", -1);
        StringBuilder rendered = new StringBuilder();
        for (int index = 0; index < lines.length; index++) {
            if (index == lines.length - 1 && lines[index].length() == 0) {
                continue;
            }
            rendered.append(renderLine(lines[index], codeFence.language(), codeHighlighting)).append('\n');
        }
        return rendered.append("</code></pre>").toString();
    }

    static String renderMermaid(int index, String source, Map<Integer, SafeHtml> renderedDiagrams) {
        SafeHtml rendered = renderedDiagrams == null
                ? null : renderedDiagrams.get(Integer.valueOf(index));
        if (rendered != null) {
            return "<div class=\"mermaid-diagram\"><div class=\"mermaid-diagram-scale\">"
                    + rendered.value() + "</div></div>";
        }
        return "<div class=\"mermaid-placeholder\" data-mermaid-index=\"" + index + "\">"
                + "<strong>Mermaid diagram</strong>"
                + "<span>Rendering in background...</span>"
                + "<pre><code class=\"language-mermaid\">" + escapeHtml(source.trim()) + "</code></pre>"
                + "</div>";
    }

    static boolean isFenceLine(String line) {
        int indent = 0;
        while (indent < line.length() && line.charAt(indent) == ' ') {
            indent++;
        }
        if (indent > 3) {
            return false;
        }
        int ticks = 0;
        while (indent + ticks < line.length() && line.charAt(indent + ticks) == '`') {
            ticks++;
        }
        return ticks >= 3;
    }

    static String openingHtml(CodeFenceInfo codeFence) {
        if (codeFence.language().length() == 0) {
            return "<pre><code>";
        }
        return "<pre><code class=\"language-" + codeFence.language() + "\">";
    }

    private static SafeHtml renderPreview(String codeBlock, CodeFenceInfo codeFence) {
        String source = codeBlock.endsWith("\n")
                ? codeBlock.substring(0, codeBlock.length() - 1) : codeBlock;
        if (codeFence.isMarkdownPreview()) {
            return new JavaSimpleMarkdownRenderer().render(
                    source,
                    CodeHighlighting.plain(),
                    MermaidRendering.plainCode(),
                    RelativeLinkRendering.disabled(),
                    RelativeImageRendering.disabled(),
                    null);
        }
        return SafeHtmlCodePreview.from(source);
    }

    private static String renderLine(String line, String language, CodeHighlighting codeHighlighting) {
        if (!codeHighlighting.isEnabled()) {
            return escapeHtml(line);
        }
        if ("java".equals(language)) {
            return JavaCodeHighlighter.highlightLine(line);
        }
        if ("kt".equals(language) || "kotlin".equals(language)) {
            return KotlinCodeHighlighter.highlightLine(line);
        }
        if ("js".equals(language) || "jsx".equals(language) || "javascript".equals(language)
                || "ts".equals(language) || "tsx".equals(language) || "typescript".equals(language)) {
            return JavaScriptCodeHighlighter.highlightLine(line);
        }
        if ("py".equals(language) || "python".equals(language)) {
            return PythonCodeHighlighter.highlightLine(line);
        }
        if ("html".equals(language) || "xml".equals(language)) {
            return MarkupCodeHighlighter.highlightLine(line);
        }
        if ("css".equals(language)) {
            return CssCodeHighlighter.highlightLine(line);
        }
        if ("yaml".equals(language) || "yml".equals(language)) {
            return YamlCodeHighlighter.highlightLine(line);
        }
        if ("json".equals(language)) {
            return JsonCodeHighlighter.highlightLine(line);
        }
        if ("sh".equals(language) || "bash".equals(language) || "shell".equals(language)) {
            return ShellCodeHighlighter.highlightLine(line);
        }
        return escapeHtml(line);
    }

    private static String escapeHtml(String text) {
        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < text.length(); index++) {
            char value = text.charAt(index);
            switch (value) {
                case '&': escaped.append("&amp;"); break;
                case '<': escaped.append("&lt;"); break;
                case '>': escaped.append("&gt;"); break;
                case '"': escaped.append("&quot;"); break;
                case '\'': escaped.append("&#39;"); break;
                default: escaped.append(value); break;
            }
        }
        return escaped.toString();
    }
}
