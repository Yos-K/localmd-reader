package io.github.yosk.mdlite.file;

public abstract class RememberedMarkdownLibrary {
    private static final NoRememberedLibrary NONE = new NoRememberedLibrary();

    private RememberedMarkdownLibrary() {
    }

    public static NoRememberedLibrary none() {
        return NONE;
    }

    public static SelectedLibrary selected(String treeUri) {
        return new SelectedLibrary(treeUri);
    }

    public static RememberedMarkdownLibrary fromStoredValue(String treeUri) {
        String safeTreeUri = treeUri == null ? "" : treeUri.trim();
        if (safeTreeUri.length() == 0) {
            return none();
        }
        return selected(safeTreeUri);
    }

    public static final class NoRememberedLibrary extends RememberedMarkdownLibrary {
        private NoRememberedLibrary() {
        }
    }

    public static final class SelectedLibrary extends RememberedMarkdownLibrary {
        private final String treeUri;

        private SelectedLibrary(String treeUri) {
            String safeTreeUri = treeUri == null ? "" : treeUri.trim();
            if (safeTreeUri.length() == 0) {
                throw new IllegalArgumentException("remembered library tree URI must not be empty");
            }
            this.treeUri = safeTreeUri;
        }

        public String treeUri() {
            return treeUri;
        }
    }
}
