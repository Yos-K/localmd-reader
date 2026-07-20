package io.github.yosk.mdlite.domain;

public final class MarkdownHeading {
    private final int level;
    private final String title;
    private final String anchorId;

    public MarkdownHeading(int level, String title, String anchorId) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("Markdown heading level must be between 1 and 6.");
        }
        String safeTitle = title == null ? "" : title.trim();
        if (safeTitle.length() == 0) {
            throw new IllegalArgumentException("Markdown heading title must not be empty.");
        }
        String safeAnchorId = anchorId == null ? "" : anchorId.trim();
        if (safeAnchorId.length() == 0) {
            throw new IllegalArgumentException("Markdown heading anchor must not be empty.");
        }
        this.level = level;
        this.title = safeTitle;
        this.anchorId = safeAnchorId;
    }

    public int level() {
        return level;
    }

    public String title() {
        return title;
    }

    public String anchorId() {
        return anchorId;
    }
}
