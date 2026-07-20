package io.github.yosk.mdlite.file;

public interface MarkdownLibraryRootStorage {
    String loadTreeUri();

    void saveTreeUri(String treeUri);

    void clearTreeUri();
}
