package io.github.yosk.mdlite.file;

public abstract class MarkdownLibraryItem {
    private final String displayName;
    private final String uri;

    private MarkdownLibraryItem(String displayName, String uri) {
        String safeDisplayName = displayName == null ? "" : displayName.trim();
        String safeUri = uri == null ? "" : uri.trim();
        if (safeDisplayName.length() == 0 || safeUri.length() == 0) {
            throw new IllegalArgumentException("library item name and URI must not be empty");
        }
        this.displayName = safeDisplayName;
        this.uri = safeUri;
    }

    public static DirectoryItem directory(String displayName, String uri) {
        return new DirectoryItem(displayName, uri);
    }

    public static DocumentItem document(String displayName, String uri) {
        if (!FileTypeDetector.isMarkdownDisplayName(displayName)) {
            throw new IllegalArgumentException("library document must have a Markdown extension");
        }
        return new DocumentItem(displayName, uri);
    }

    public final String displayName() {
        return displayName;
    }

    public final String uri() {
        return uri;
    }

    public static final class DirectoryItem extends MarkdownLibraryItem {
        private DirectoryItem(String displayName, String uri) {
            super(displayName, uri);
        }
    }

    public static final class DocumentItem extends MarkdownLibraryItem {
        private DocumentItem(String displayName, String uri) {
            super(displayName, uri);
        }
    }
}
