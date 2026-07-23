package io.github.yosk.mdlite.domain;

public final class MarkdownStyle {
    private final boolean bold;
    private final boolean italic;
    private final boolean underline;
    private final String linkUrl;
    private final int headingLevel;
    private final boolean bulletListItem;

    private MarkdownStyle(boolean bold, boolean italic, boolean underline, String linkUrl, int headingLevel, boolean bulletListItem) {
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.linkUrl = linkUrl == null ? "" : linkUrl;
        this.headingLevel = headingLevel;
        this.bulletListItem = bulletListItem;
    }

    public static MarkdownStyle plain() {
        return new MarkdownStyle(false, false, false, "", 0, false);
    }

    public MarkdownStyle withBold() {
        return new MarkdownStyle(true, italic, underline, linkUrl, headingLevel, bulletListItem);
    }

    public MarkdownStyle withItalic() {
        return new MarkdownStyle(bold, true, underline, linkUrl, headingLevel, bulletListItem);
    }

    public MarkdownStyle withUnderline() {
        return new MarkdownStyle(bold, italic, true, linkUrl, headingLevel, bulletListItem);
    }

    public MarkdownStyle withLink(String url) {
        return new MarkdownStyle(bold, italic, underline, url, headingLevel, bulletListItem);
    }

    public MarkdownStyle withHeadingLevel(int level) {
        int safeLevel = level < 1 ? 0 : Math.min(6, level);
        return new MarkdownStyle(bold, italic, underline, linkUrl, safeLevel, bulletListItem);
    }

    public MarkdownStyle withBulletListItem() {
        return new MarkdownStyle(bold, italic, underline, linkUrl, headingLevel, true);
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

    int headingLevel() {
        return headingLevel;
    }

    boolean isBulletListItem() {
        return bulletListItem;
    }

    boolean isPlain() {
        return !bold
                && !italic
                && !underline
                && linkUrl.length() == 0
                && headingLevel == 0
                && !bulletListItem;
    }
}
