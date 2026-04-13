package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;

public final class JavaSimpleMarkdownRenderer {
    private static final int LIST_NONE = 0;
    private static final int LIST_UNORDERED = 1;
    private static final int LIST_ORDERED = 2;

    public SafeHtml render(String markdown) {
        String source = markdown == null ? "" : markdown;
        StringBuilder html = new StringBuilder();
        String[] lines = source.split("\\r?\\n", -1);
        boolean inCodeBlock = false;
        StringBuilder paragraph = new StringBuilder();
        int openList = LIST_NONE;

        for (String line : lines) {
            if (isFenceLine(line)) {
                if (inCodeBlock) {
                    html.append("</code></pre>");
                    inCodeBlock = false;
                } else {
                    flushParagraph(html, paragraph);
                    openList = closeList(html, openList);
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
                openList = closeList(html, openList);
                continue;
            }

            int headingLevel = headingLevel(line);
            if (headingLevel > 0) {
                flushParagraph(html, paragraph);
                openList = closeList(html, openList);
                String headingText = line.substring(headingLevel + 1).trim();
                html.append("<h").append(headingLevel).append(">")
                        .append(renderInline(headingText))
                        .append("</h").append(headingLevel).append(">");
                continue;
            }

            if (line.equals("---")) {
                flushParagraph(html, paragraph);
                openList = closeList(html, openList);
                html.append("<hr>");
                continue;
            }

            if (line.startsWith("> ")) {
                flushParagraph(html, paragraph);
                openList = closeList(html, openList);
                html.append("<blockquote>").append(renderInline(line.substring(2).trim())).append("</blockquote>");
                continue;
            }

            if (line.startsWith("- ")) {
                flushParagraph(html, paragraph);
                if (openList != LIST_UNORDERED) {
                    openList = closeList(html, openList);
                    html.append("<ul>");
                    openList = LIST_UNORDERED;
                }
                html.append("<li>").append(renderInline(line.substring(2).trim())).append("</li>");
                continue;
            }

            int orderedMarkerLength = orderedMarkerLength(line);
            if (orderedMarkerLength > 0) {
                flushParagraph(html, paragraph);
                if (openList != LIST_ORDERED) {
                    openList = closeList(html, openList);
                    html.append("<ol>");
                    openList = LIST_ORDERED;
                }
                html.append("<li>").append(renderInline(line.substring(orderedMarkerLength).trim())).append("</li>");
                continue;
            }

            openList = closeList(html, openList);
            if (paragraph.length() > 0) {
                paragraph.append(' ');
            }
            paragraph.append(line.trim());
        }

        if (inCodeBlock) {
            html.append("</code></pre>");
        }
        closeList(html, openList);
        flushParagraph(html, paragraph);

        return SafeHtml.fromTrustedRendererOutput(html.toString());
    }

    private static boolean isFenceLine(String line) {
        return line.equals("```") || (line.startsWith("```") && line.trim().length() > 3);
    }

    private static int headingLevel(String line) {
        int level = 0;
        while (level < line.length() && line.charAt(level) == '#') {
            level++;
        }
        if (level < 1 || level > 6) {
            return 0;
        }
        if (level >= line.length() || line.charAt(level) != ' ') {
            return 0;
        }
        return level;
    }

    private static int orderedMarkerLength(String line) {
        int index = 0;
        while (index < line.length() && Character.isDigit(line.charAt(index))) {
            index++;
        }
        if (index == 0 || index + 1 >= line.length()) {
            return 0;
        }
        if (line.charAt(index) == '.' && line.charAt(index + 1) == ' ') {
            return index + 2;
        }
        return 0;
    }

    private static int closeList(StringBuilder html, int openList) {
        if (openList == LIST_UNORDERED) {
            html.append("</ul>");
        } else if (openList == LIST_ORDERED) {
            html.append("</ol>");
        }
        return LIST_NONE;
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
