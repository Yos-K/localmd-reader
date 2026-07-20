package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.domain.MermaidDiagramBlock;
import io.github.yosk.mdlite.domain.SafeHtml;

final class MermaidRenderErrorHtml {
    private MermaidRenderErrorHtml() {
    }

    static SafeHtml from(MermaidDiagramBlock block, String reason) {
        return SafeHtml.fromTrustedRendererOutput(
                "<div class=\"mermaid-error\">"
                        + "<strong>Unable to render this Mermaid diagram.</strong>"
                        + "<span>" + escapeHtml(normalizedReason(reason)) + "</span>"
                        + "<pre><code class=\"language-mermaid\">" + escapeHtml(block.source()) + "</code></pre>"
                        + "</div>");
    }

    private static String normalizedReason(String reason) {
        String normalized = reason == null ? "" : reason.trim();
        if (normalized.length() == 0) {
            return "The diagram syntax is not supported or contains an error.";
        }
        return normalized;
    }

    private static String escapeHtml(String text) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            escaped.append(escapeHtmlChar(text.charAt(i)));
        }
        return escaped.toString();
    }

    private static String escapeHtmlChar(char value) {
        switch (value) {
            case '&':
                return "&amp;";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "&quot;";
            default:
                return String.valueOf(value);
        }
    }
}
