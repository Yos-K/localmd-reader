package io.github.yosk.mdlite.domain;

public final class TableOfContentsItem {
    private final MarkdownHeading heading;
    private final String label;

    TableOfContentsItem(MarkdownHeading heading, String label) {
        if (heading == null) {
            throw new IllegalArgumentException("Table of contents heading must not be null.");
        }
        String safeLabel = label == null ? "" : label;
        if (safeLabel.trim().length() == 0) {
            throw new IllegalArgumentException("Table of contents label must not be empty.");
        }
        this.heading = heading;
        this.label = safeLabel;
    }

    public MarkdownHeading heading() {
        return heading;
    }

    public String label() {
        return label;
    }
}
