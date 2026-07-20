package io.github.yosk.mdlite.viewer;

public final class DocumentSearchQuery {
    private final String text;

    private DocumentSearchQuery(String text) {
        this.text = text;
    }

    public static DocumentSearchQuery from(String rawText) {
        return new DocumentSearchQuery(rawText == null ? "" : rawText.trim());
    }

    public boolean isActive() {
        return !text.isEmpty();
    }

    public String text() {
        return text;
    }
}
