package io.github.yosk.mdlite.domain;

public final class StyledMarkdownText {
    private final StringBuilder markdown = new StringBuilder();

    public StyledMarkdownText append(String text, MarkdownStyle style) {
        String safeText = text == null ? "" : text;
        MarkdownStyle safeStyle = style == null ? MarkdownStyle.plain() : style;
        markdown.append(applyStyle(safeText, safeStyle));
        return this;
    }

    public String value() {
        return markdown.toString();
    }

    private static String applyStyle(String text, MarkdownStyle style) {
        if (style.isBulletListItem()) {
            return bulletItems(text, style);
        }
        if (isTabSeparatedTable(text)) {
            return table(text);
        }
        if (style.isPlain()) {
            return text;
        }

        String styled = escapeMarkdown(text);
        if (style.isBold()) {
            styled = "**" + styled + "**";
        }
        if (style.isItalic()) {
            styled = "_" + styled + "_";
        }
        if (style.isUnderline()) {
            styled = "<u>" + styled + "</u>";
        }
        if (style.linkUrl().length() > 0) {
            styled = "[" + styled + "](" + escapeUrl(style.linkUrl()) + ")";
        }
        if (style.headingLevel() > 0) {
            styled = headingPrefix(style.headingLevel()) + styled.trim() + "\n\n";
        }
        return styled;
    }

    private static String bulletItems(String text, MarkdownStyle style) {
        String[] lines = text.split("\\n", -1);
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().length() > 0) {
                list.append("- ").append(applyInlineStyle(escapeMarkdown(lines[i].trim()), style)).append('\n');
            }
        }
        return list.toString();
    }

    private static String applyInlineStyle(String text, MarkdownStyle style) {
        String styled = text;
        if (style.isBold()) {
            styled = "**" + styled + "**";
        }
        if (style.isItalic()) {
            styled = "_" + styled + "_";
        }
        if (style.isUnderline()) {
            styled = "<u>" + styled + "</u>";
        }
        if (style.linkUrl().length() > 0) {
            styled = "[" + styled + "](" + escapeUrl(style.linkUrl()) + ")";
        }
        return styled;
    }

    private static boolean isTabSeparatedTable(String text) {
        String[] lines = text.split("\\n", -1);
        return lines.length >= 2 && lines[0].indexOf('\t') >= 0 && lines[1].indexOf('\t') >= 0;
    }

    private static String table(String text) {
        String[] lines = text.split("\\n", -1);
        StringBuilder table = new StringBuilder();
        appendTableRow(table, lines[0]);
        appendSeparatorRow(table, lines[0]);
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().length() > 0) {
                appendTableRow(table, lines[i]);
            }
        }
        return trimTrailingNewline(table.toString());
    }

    private static void appendTableRow(StringBuilder table, String line) {
        String[] cells = line.split("\\t", -1);
        table.append('|');
        for (int i = 0; i < cells.length; i++) {
            table.append(' ').append(escapeTableCell(cells[i].trim())).append(" |");
        }
        table.append('\n');
    }

    private static void appendSeparatorRow(StringBuilder table, String headerLine) {
        String[] cells = headerLine.split("\\t", -1);
        table.append('|');
        for (int i = 0; i < cells.length; i++) {
            table.append(" --- |");
        }
        table.append('\n');
    }

    private static String escapeTableCell(String cell) {
        return escapeMarkdown(cell).replace("|", "\\|");
    }

    private static String trimTrailingNewline(String value) {
        if (value.endsWith("\n")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }

    private static String headingPrefix(int level) {
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < level; i++) {
            prefix.append('#');
        }
        return prefix.append(' ').toString();
    }

    private static String escapeMarkdown(String text) {
        return text.replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("_", "\\_")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("`", "\\`");
    }

    private static String escapeUrl(String url) {
        return url.replace(")", "%29").replace(" ", "%20");
    }
}
