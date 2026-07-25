package io.github.yosk.mdlite.viewer;

public interface PinnedDocumentRepository {
    void pinDocument(String displayName, String uri);
    void unpinDocument(String uri);
    boolean isPinnedDocument(String uri);
    void clearPinnedDocuments();
}
