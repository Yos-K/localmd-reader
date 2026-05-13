package io.github.yosk.mdlite.domain;

public final class MarkdownStyle {
    private final boolean bold;
    private final boolean italic;
    private final boolean underline;
    private final String linkUrl;

    private MarkdownStyle(boolean bold, boolean italic, boolean underline, String linkUrl) {
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.linkUrl = linkUrl == null ? "" : linkUrl;
    }

    public static MarkdownStyle plain() {
        return new MarkdownStyle(false, false, false, "");
    }

    public MarkdownStyle withBold() {
        return new MarkdownStyle(true, italic, underline, linkUrl);
    }

    public MarkdownStyle withItalic() {
        return new MarkdownStyle(bold, true, underline, linkUrl);
    }

    public MarkdownStyle withUnderline() {
        return new MarkdownStyle(bold, italic, true, linkUrl);
    }

    public MarkdownStyle withLink(String url) {
        return new MarkdownStyle(bold, italic, underline, url);
    }

    boolean isBold() {
        return bold;
    }

    boolean isItalic() {
        return italic;
    }

    boolean isUnderline() {
        return underline;
    }

    String linkUrl() {
        return linkUrl;
    }
}
