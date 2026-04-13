package io.github.yosk.mdlite.domain;

public final class OpenDocumentTab {
    private final String title;
    private final String uri;
    private final SafeHtml document;

    private OpenDocumentTab(String title, String uri, SafeHtml document) {
        this.title = title;
        this.uri = uri;
        this.document = document;
    }

    public static OpenDocumentTab of(String title, String uri, SafeHtml document) {
        String safeTitle = title == null ? "" : title.trim();
        String safeUri = uri == null ? "" : uri.trim();
        if (safeTitle.length() == 0) {
            safeTitle = "Untitled Markdown";
        }
        if (safeUri.length() == 0) {
            throw new IllegalArgumentException("open tab URI must not be empty");
        }
        if (document == null) {
            throw new IllegalArgumentException("open tab document must not be null");
        }
        return new OpenDocumentTab(safeTitle, safeUri, document);
    }

    public String title() {
        return title;
    }

    public String uri() {
        return uri;
    }

    public SafeHtml document() {
        return document;
    }
}
