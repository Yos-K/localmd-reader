package io.github.yosk.mdlite.file;

public final class MarkdownLibraryRootStore {
    private final MarkdownLibraryRootStorage storage;

    public MarkdownLibraryRootStore(MarkdownLibraryRootStorage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("Markdown library root storage must not be null");
        }
        this.storage = storage;
    }

    public RememberedMarkdownLibrary load() {
        return RememberedMarkdownLibrary.fromStoredValue(storage.loadTreeUri());
    }

    public void remember(RememberedMarkdownLibrary.SelectedLibrary selectedLibrary) {
        if (selectedLibrary == null) {
            throw new IllegalArgumentException("selected Markdown library must not be null");
        }
        storage.saveTreeUri(selectedLibrary.treeUri());
    }

    public void forget() {
        storage.clearTreeUri();
    }
}
