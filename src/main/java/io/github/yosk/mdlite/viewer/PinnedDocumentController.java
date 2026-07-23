package io.github.yosk.mdlite.viewer;

public final class PinnedDocumentController implements TabPinningDecision.Handler {
    private final PinnedDocumentRepository repository;
    private final Host host;

    public PinnedDocumentController(PinnedDocumentRepository repository, Host host) {
        if (repository == null || host == null) {
            throw new IllegalArgumentException("pinned documents require a repository and host");
        }
        this.repository = repository;
        this.host = host;
    }

    public TabPinningDecision decision(OpenDocumentTab tab) {
        return TabPinningDecision.from(host.pinnedDocumentsAvailable(), tab,
                repository.isPinnedDocument(tab.uri()));
    }

    public boolean isPinned(OpenDocumentTab tab) {
        return tab instanceof OpenDocumentTab.FileDocumentTab
                && repository.isPinnedDocument(tab.uri());
    }

    public void pinCurrent(OpenDocumentTab tab) {
        TabPinningDecision.from(host.pinnedDocumentsAvailable(), tab, false).perform(this);
    }

    public void unpinCurrent(OpenDocumentTab tab) {
        TabPinningDecision.from(host.pinnedDocumentsAvailable(), tab, true).perform(this);
    }

    @Override
    public void pin(OpenDocumentTab.FileDocumentTab tab) {
        repository.pinDocument(tab.title(), tab.uri());
        host.refreshPinnedDocuments(host.viewerText().currentFilePinned());
    }

    @Override
    public void unpin(OpenDocumentTab.FileDocumentTab tab) {
        repository.unpinDocument(tab.uri());
        host.refreshPinnedDocuments(host.viewerText().currentFileUnpinned());
    }

    public void unpin(String documentUri) {
        repository.unpinDocument(documentUri);
        host.refreshPinnedDocuments(host.viewerText().currentFileUnpinned());
    }

    public void clear() {
        repository.clearPinnedDocuments();
        host.refreshPinnedDocuments(host.viewerText().pinnedFilesCleared());
    }

    public interface Host {
        boolean pinnedDocumentsAvailable();
        ViewerText viewerText();
        void refreshPinnedDocuments(String message);
    }
}
