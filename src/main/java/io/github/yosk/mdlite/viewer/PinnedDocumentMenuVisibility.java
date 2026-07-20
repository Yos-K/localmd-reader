package io.github.yosk.mdlite.viewer;

public final class PinnedDocumentMenuVisibility {
    private final boolean pinnedDocumentsAvailable;
    private final boolean activeTabIsFile;
    private final boolean activeFileIsPinned;

    private PinnedDocumentMenuVisibility(
            boolean pinnedDocumentsAvailable,
            boolean activeTabIsFile,
            boolean activeFileIsPinned) {
        this.pinnedDocumentsAvailable = pinnedDocumentsAvailable;
        this.activeTabIsFile = activeTabIsFile;
        this.activeFileIsPinned = activeFileIsPinned;
    }

    public static PinnedDocumentMenuVisibility of(
            boolean pinnedDocumentsAvailable,
            boolean activeTabIsFile,
            boolean activeFileIsPinned) {
        return new PinnedDocumentMenuVisibility(
                pinnedDocumentsAvailable,
                activeTabIsFile,
                activeFileIsPinned);
    }

    public boolean canPinCurrentFile() {
        return pinnedDocumentsAvailable && activeTabIsFile && !activeFileIsPinned;
    }

    public boolean canUnpinCurrentFile() {
        return pinnedDocumentsAvailable && activeTabIsFile && activeFileIsPinned;
    }

    public boolean canOpenPinnedFiles() {
        return pinnedDocumentsAvailable;
    }
}
