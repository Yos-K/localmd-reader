package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.DocumentRenderingSession;

public abstract class DocumentTabCloseResult {
    private final OpenDocumentTabs tabs;

    private DocumentTabCloseResult(OpenDocumentTabs tabs) {
        if (tabs == null) {
            throw new IllegalArgumentException("tab close result requires open tabs");
        }
        this.tabs = tabs;
    }

    static DocumentTabCloseResult closed(OpenDocumentTabs tabs, OpenDocumentTab closedTab) {
        return new Closed(tabs, closedTab);
    }

    static DocumentTabCloseResult unchanged(OpenDocumentTabs tabs) {
        return new Unchanged(tabs);
    }

    public final OpenDocumentTabs tabs() {
        return tabs;
    }

    public abstract DocumentRenderingSession renderingSessionAfter(DocumentRenderingSession session);

    private static final class Closed extends DocumentTabCloseResult {
        private final OpenDocumentTab closedTab;

        private Closed(OpenDocumentTabs tabs, OpenDocumentTab closedTab) {
            super(tabs);
            if (closedTab == null) {
                throw new IllegalArgumentException("closed tab result requires the closed tab");
            }
            this.closedTab = closedTab;
        }

        @Override
        public DocumentRenderingSession renderingSessionAfter(DocumentRenderingSession session) {
            requireSession(session);
            return session.close(closedTab.documentUri());
        }
    }

    private static final class Unchanged extends DocumentTabCloseResult {
        private Unchanged(OpenDocumentTabs tabs) {
            super(tabs);
        }

        @Override
        public DocumentRenderingSession renderingSessionAfter(DocumentRenderingSession session) {
            requireSession(session);
            return session;
        }
    }

    private static void requireSession(DocumentRenderingSession session) {
        if (session == null) {
            throw new IllegalArgumentException("tab close requires a rendering session");
        }
    }
}
