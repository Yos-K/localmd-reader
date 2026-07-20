package io.github.yosk.mdlite.file;

public abstract class MarkdownLibraryLocation {
    private final String treeUri;
    private final String directoryUri;
    private final String displayName;
    private final MarkdownLibraryPath path;

    private MarkdownLibraryLocation(String treeUri, String directoryUri, String displayName,
            MarkdownLibraryPath path) {
        this.treeUri = required(treeUri, "tree URI");
        this.directoryUri = required(directoryUri, "directory URI");
        this.displayName = required(displayName, "directory display name");
        if (path == null) {
            throw new IllegalArgumentException("Markdown library path must not be null");
        }
        this.path = path;
    }

    public static RootLocation root(String treeUri, String displayName) {
        return new RootLocation(treeUri, displayName);
    }

    public final String treeUri() {
        return treeUri;
    }

    public final String directoryUri() {
        return directoryUri;
    }

    public final String displayName() {
        return displayName;
    }

    public final MarkdownLibraryPath path() {
        return path;
    }

    public abstract MarkdownLibraryLocation back();

    public final NestedLocation enter(MarkdownLibraryItem.DirectoryItem directory) {
        if (directory == null) {
            throw new IllegalArgumentException("directory item must not be null");
        }
        return new NestedLocation(treeUri, directory.uri(), directory.displayName(), this);
    }

    private static String required(String value, String label) {
        String safe = value == null ? "" : value.trim();
        if (safe.length() == 0) {
            throw new IllegalArgumentException(label + " must not be empty");
        }
        return safe;
    }

    public static final class RootLocation extends MarkdownLibraryLocation {
        private RootLocation(String treeUri, String displayName) {
            super(treeUri, treeUri, displayName, MarkdownLibraryPath.root(displayName));
        }

        @Override
        public RootLocation back() {
            return this;
        }
    }

    public static final class NestedLocation extends MarkdownLibraryLocation {
        private final MarkdownLibraryLocation parent;

        private NestedLocation(String treeUri, String directoryUri, String displayName,
                MarkdownLibraryLocation parent) {
            super(treeUri, directoryUri, displayName, parent.path().append(displayName));
            this.parent = parent;
        }

        @Override
        public MarkdownLibraryLocation back() {
            return parent;
        }
    }
}
