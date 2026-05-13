package io.github.yosk.mdlite.domain;

public final class StyledMarkdownText {
    private final StringBuilder markdown = new StringBuilder();

    public StyledMarkdownText append(String text, MarkdownStyle style) {
        String safeText = text == null ? "" : text;
        MarkdownStyle safeStyle = style == null ? MarkdownStyle.plain() : style;
        markdown.append(applyStyle(escapeMarkdown(safeText), safeStyle));
        return this;
    }

    public String value() {
        return markdown.toString();
    }

    private static String applyStyle(String text, MarkdownStyle style) {
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
