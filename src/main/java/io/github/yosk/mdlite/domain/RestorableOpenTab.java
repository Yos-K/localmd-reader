package io.github.yosk.mdlite.domain;

public final class RestorableOpenTab {
    private final String title;
    private final String uri;

    private RestorableOpenTab(String title, String uri) {
        this.title = title;
        this.uri = uri;
    }

    public static RestorableOpenTab of(String title, String uri) {
        String safeTitle = title == null ? "" : title.trim();
        String safeUri = uri == null ? "" : uri.trim();
        if (safeTitle.length() == 0) {
            safeTitle = "Untitled Markdown";
        }
        if (safeUri.length() == 0) {
            throw new IllegalArgumentException("restorable open tab URI must not be empty");
        }
        return new RestorableOpenTab(safeTitle, safeUri);
    }

    public String title() {
        return title;
    }

    public String uri() {
        return uri;
    }
}
