package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;

public final class JavaSimpleMarkdownRenderer {
    private static final int LIST_NONE = 0;
    private static final int LIST_UNORDERED = 1;
    private static final int LIST_ORDERED = 2;
    private static final int LIST_CHECKLIST = 3;

    public SafeHtml render(String markdown) {
        String source = markdown == null ? "" : markdown;
        StringBuilder html = new StringBuilder();
        String[] lines = source.split("\\r?\\n", -1);
        boolean inCodeBlock = false;
        StringBuilder paragraph = new StringBuilder();
        int openList = LIST_NONE;

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
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

            if (lineIndex + 1 < lines.length && isTableHeaderLine(line) && isTableSeparatorLine(lines[lineIndex + 1])) {
                flushParagraph(html, paragraph);
                openList = closeList(html, openList);
                String[] headerCells = splitTableCells(line);
                html.append("<div class=\"table-scroll\"><table><thead><tr>");
                appendTableCells(html, headerCells, "th");
                html.append("</tr></thead><tbody>");
                lineIndex += 2;
                while (lineIndex < lines.length && isTableHeaderLine(lines[lineIndex])) {
                    html.append("<tr>");
                    appendTableCells(html, splitTableCells(lines[lineIndex]), "td");
                    html.append("</tr>");
                    lineIndex++;
                }
                lineIndex--;
                html.append("</tbody></table></div>");
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
                String checkbox = checklistCheckboxHtml(line);
                if (checkbox != null) {
                    if (openList != LIST_CHECKLIST) {
                        openList = closeList(html, openList);
                        html.append("<ul class=\"checklist\">");
                        openList = LIST_CHECKLIST;
                    }
                    html.append("<li>").append(checkbox).append(' ')
                            .append(renderInline(line.substring(6).trim())).append("</li>");
                    continue;
                }
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

    private static boolean isTableHeaderLine(String line) {
        String trimmed = line.trim();
        return trimmed.indexOf('|') >= 0 && splitTableCells(trimmed).length > 1;
    }

    private static boolean isTableSeparatorLine(String line) {
        String[] cells = splitTableCells(line);
        if (cells.length < 2) {
            return false;
        }
        for (int i = 0; i < cells.length; i++) {
            if (!isTableSeparatorCell(cells[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isTableSeparatorCell(String cell) {
        String trimmed = cell.trim();
        if (trimmed.startsWith(":")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith(":")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.length() < 3) {
            return false;
        }
        for (int i = 0; i < trimmed.length(); i++) {
            if (trimmed.charAt(i) != '-') {
                return false;
            }
        }
        return true;
    }

    private static String[] splitTableCells(String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("|")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith("|")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        String[] rawCells = trimmed.split("\\|", -1);
        for (int i = 0; i < rawCells.length; i++) {
            rawCells[i] = rawCells[i].trim();
        }
        return rawCells;
    }

    private static void appendTableCells(StringBuilder html, String[] cells, String tag) {
        for (int i = 0; i < cells.length; i++) {
            html.append('<').append(tag).append('>')
                    .append(renderInline(cells[i]))
                    .append("</").append(tag).append('>');
        }
    }

    private static int closeList(StringBuilder html, int openList) {
        if (openList == LIST_UNORDERED) {
            html.append("</ul>");
        } else if (openList == LIST_ORDERED) {
            html.append("</ol>");
        } else if (openList == LIST_CHECKLIST) {
            html.append("</ul>");
        }
        return LIST_NONE;
    }

    private static String checklistCheckboxHtml(String line) {
        if (line.length() < 6) {
            return null;
        }
        if (!line.startsWith("- [") || line.charAt(4) != ']' || line.charAt(5) != ' ') {
            return null;
        }
        char marker = line.charAt(3);
        if (marker == ' ') {
            return "<input type=\"checkbox\" disabled>";
        }
        if (marker == 'x' || marker == 'X') {
            return "<input type=\"checkbox\" checked disabled>";
        }
        return null;
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
                int linkEnd = appendMarkdownLinkIfPresent(out, text, i);
                if (linkEnd >= i) {
                    i = linkEnd;
                    continue;
                }
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

    private static int appendMarkdownLinkIfPresent(StringBuilder out, String text, int index) {
        if (text.charAt(index) != '[') {
            return -1;
        }
        int labelEnd = text.indexOf(']', index + 1);
        if (labelEnd < 0 || labelEnd + 1 >= text.length() || text.charAt(labelEnd + 1) != '(') {
            return -1;
        }
        int urlEnd = text.indexOf(')', labelEnd + 2);
        if (urlEnd < 0) {
            return -1;
        }

        String label = text.substring(index + 1, labelEnd);
        String url = text.substring(labelEnd + 2, urlEnd).trim();
        if (isSafeLinkUrl(url)) {
            out.append("<a href=\"").append(escapeHtml(url)).append("\">")
                    .append(renderInline(label))
                    .append("</a>");
        } else {
            out.append(renderInline(label));
        }
        return urlEnd;
    }

    private static boolean isSafeLinkUrl(String url) {
        String lower = url.toLowerCase();
        return lower.startsWith("https://") || lower.startsWith("http://");
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
