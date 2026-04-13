package io.github.yosk.mdlite.domain;

public final class RecentDocument {
    private final String displayName;
    private final String uri;

    private RecentDocument(String displayName, String uri) {
        this.displayName = displayName;
        this.uri = uri;
    }

    public static RecentDocument of(String displayName, String uri) {
        String safeDisplayName = displayName == null ? "" : displayName.trim();
        String safeUri = uri == null ? "" : uri.trim();
        if (safeDisplayName.length() == 0) {
            safeDisplayName = "Untitled Markdown";
        }
        if (safeUri.length() == 0) {
            throw new IllegalArgumentException("recent document URI must not be empty");
        }
        return new RecentDocument(safeDisplayName, safeUri);
    }

    public String displayName() {
        return displayName;
    }

    public String uri() {
        return uri;
    }
}
