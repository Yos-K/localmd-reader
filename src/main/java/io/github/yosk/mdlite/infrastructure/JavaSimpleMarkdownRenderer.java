package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;

public final class JavaSimpleMarkdownRenderer {
    public SafeHtml render(String markdown) {
        String source = markdown == null ? "" : markdown;
        StringBuilder html = new StringBuilder();
        String[] lines = source.split("\\r?\\n", -1);
        boolean inCodeBlock = false;
        StringBuilder paragraph = new StringBuilder();

        for (String line : lines) {
            if (line.equals("```")) {
                if (inCodeBlock) {
                    html.append("</code></pre>");
                    inCodeBlock = false;
                } else {
                    flushParagraph(html, paragraph);
                    html.append("<pre><code>");
                    inCodeBlock = true;
                }
                continue;
            }

            if (inCodeBlock) {
                html.append(escapeHtml(line)).append('\n');
                continue;
            }

            if (line.trim().isEmpty()) {
                flushParagraph(html, paragraph);
                continue;
            }

            if (line.startsWith("# ")) {
                flushParagraph(html, paragraph);
                html.append("<h1>").append(renderInline(line.substring(2).trim())).append("</h1>");
                continue;
            }

            if (paragraph.length() > 0) {
                paragraph.append(' ');
            }
            paragraph.append(line.trim());
        }

        if (inCodeBlock) {
            html.append("</code></pre>");
        }
        flushParagraph(html, paragraph);

        return SafeHtml.fromTrustedRendererOutput(html.toString());
    }

    private static void flushParagraph(StringBuilder html, StringBuilder paragraph) {
        if (paragraph.length() == 0) {
            return;
        }
        html.append("<p>").append(renderInline(paragraph.toString())).append("</p>");
        paragraph.setLength(0);
    }

    private static String renderInline(String text) {
        StringBuilder out = new StringBuilder();
        StringBuilder code = null;

        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == '`') {
                if (code == null) {
                    code = new StringBuilder();
                } else {
                    out.append("<code>").append(escapeHtml(code.toString())).append("</code>");
                    code = null;
                }
                continue;
            }

            if (code == null) {
                out.append(escapeHtmlChar(current));
            } else {
                code.append(current);
            }
        }

        if (code != null) {
            out.append('`').append(escapeHtml(code.toString()));
        }

        return out.toString();
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
            case '\'':
                return "&#39;";
            default:
                return String.valueOf(value);
        }
    }
}
