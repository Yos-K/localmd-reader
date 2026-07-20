package io.github.yosk.mdlite.file;

public abstract class FolderDocumentEntry {
    private final String displayName;
    private final String uri;

    private FolderDocumentEntry(String displayName, String uri) {
        String safeDisplayName = displayName == null ? "" : displayName.trim();
        String safeUri = uri == null ? "" : uri.trim();
        if (safeDisplayName.length() == 0) {
            throw new IllegalArgumentException("folder entry display name must not be empty");
        }
        if (safeUri.length() == 0) {
            throw new IllegalArgumentException("folder entry URI must not be empty");
        }
        this.displayName = safeDisplayName;
        this.uri = safeUri;
    }

    public static MarkdownFileEntry markdownFile(String displayName, String uri) {
        if (!FileTypeDetector.isMarkdownDisplayName(displayName)) {
            throw new IllegalArgumentException("folder Markdown file must have .md or .markdown extension");
        }
        return new MarkdownFileEntry(displayName, uri);
    }

    public static DirectoryEntry directory(String displayName, String uri) {
        return new DirectoryEntry(displayName, uri);
    }

    public static UnsupportedFileEntry unsupportedFile(String displayName, String uri) {
        if (FileTypeDetector.isMarkdownDisplayName(displayName)) {
            throw new IllegalArgumentException("unsupported folder file must not be Markdown");
        }
        return new UnsupportedFileEntry(displayName, uri);
    }

    public final String displayName() {
        return displayName;
    }

    public final String uri() {
        return uri;
    }

    public static final class MarkdownFileEntry extends FolderDocumentEntry {
        private MarkdownFileEntry(String displayName, String uri) {
            super(displayName, uri);
        }
    }

    public static final class DirectoryEntry extends FolderDocumentEntry {
        private DirectoryEntry(String displayName, String uri) {
            super(displayName, uri);
        }
    }

    public static final class UnsupportedFileEntry extends FolderDocumentEntry {
        private UnsupportedFileEntry(String displayName, String uri) {
            super(displayName, uri);
        }
    }
}
