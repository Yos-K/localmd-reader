package io.github.yosk.mdlite.viewer;

public final class ClipboardMarkdownItem {
    private final String title;
    private final String markdown;

    public ClipboardMarkdownItem(String title, String markdown) {
        this.title = title;
        this.markdown = markdown;
    }

    public String title() {
        return title;
    }

    public String markdown() {
        return markdown;
    }
}
