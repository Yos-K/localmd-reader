package io.github.yosk.mdlite.domain;

public final class MarkdownDraftFileName {
    private final String value;

    private MarkdownDraftFileName(String value) {
        this.value = value;
    }

    public static MarkdownDraftFileName fromTitle(String title, int sequence) {
        String base = title == null || title.trim().length() == 0 ? "created-markdown" : title.trim();
        String sanitized = base.replaceAll("[^A-Za-z0-9._-]+", "-").replaceAll("-+", "-");
        String trimmed = sanitized.replaceAll("^-|-$", "");
        String safeBase = trimmed.length() == 0 ? "created-markdown" : trimmed;
        String suffix = sequence <= 1 ? "" : "-" + sequence;
        return new MarkdownDraftFileName(safeBase + suffix + ".md");
    }

    public String value() {
        return value;
    }
}
