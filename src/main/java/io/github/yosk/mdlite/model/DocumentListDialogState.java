package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.file.PinnedDocuments;
import io.github.yosk.mdlite.file.RecentDocument;
import io.github.yosk.mdlite.file.RecentDocuments;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DocumentListDialogState {
    private static final Closed CLOSED = new Closed();

    private DocumentListDialogState() {
    }

    public static Closed closed() {
        return CLOSED;
    }

    public static Recent recent(RecentDocuments documents) {
        if (documents == null) {
            throw new IllegalArgumentException("recent document dialog requires documents");
        }
        return new Recent(documents.items());
    }

    public static Pinned pinned(PinnedDocuments documents) {
        if (documents == null) {
            throw new IllegalArgumentException("pinned document dialog requires documents");
        }
        return new Pinned(documents.items());
    }

    public static PinnedActions pinnedActions(RecentDocument document) {
        return new PinnedActions(document);
    }

    public abstract DocumentListCommand select(int index);

    public abstract DocumentListCommand secondaryAction();

    public final Closed close() {
        return CLOSED;
    }

    public static final class Closed extends DocumentListDialogState {
        private Closed() {
        }

        @Override
        public DocumentListCommand.None select(int index) {
            return DocumentListCommand.none();
        }

        @Override
        public DocumentListCommand.None secondaryAction() {
            return DocumentListCommand.none();
        }
    }

    private abstract static class Open extends DocumentListDialogState {
        private final List<RecentDocument> documents;

        private Open(List<RecentDocument> documents) {
            this.documents = Collections.unmodifiableList(
                    new ArrayList<RecentDocument>(documents));
        }

        @Override
        public final DocumentListCommand select(int index) {
            if (index < 0 || index >= documents.size()) {
                return DocumentListCommand.none();
            }
            return selectedDocument(documents.get(index));
        }

        protected DocumentListCommand selectedDocument(RecentDocument document) {
            return DocumentListCommand.open(document);
        }
    }

    public static final class Recent extends Open {
        private Recent(List<RecentDocument> documents) {
            super(documents);
        }

        @Override
        public DocumentListCommand.ClearRecent secondaryAction() {
            return DocumentListCommand.clearRecent();
        }
    }

    public static final class Pinned extends Open {
        private Pinned(List<RecentDocument> documents) {
            super(documents);
        }

        @Override
        protected DocumentListCommand selectedDocument(RecentDocument document) {
            return DocumentListCommand.choosePinnedDocumentAction(document);
        }

        @Override
        public DocumentListCommand.ClearPinned secondaryAction() {
            return DocumentListCommand.clearPinned();
        }
    }

    public static final class PinnedActions extends DocumentListDialogState {
        private final RecentDocument document;

        private PinnedActions(RecentDocument document) {
            if (document == null) {
                throw new IllegalArgumentException("pinned-document actions require a document");
            }
            this.document = document;
        }

        @Override
        public DocumentListCommand select(int index) {
            if (index == 0) {
                return DocumentListCommand.open(document);
            }
            if (index == 1) {
                return DocumentListCommand.unpin(document);
            }
            return DocumentListCommand.none();
        }

        @Override
        public DocumentListCommand.None secondaryAction() {
            return DocumentListCommand.none();
        }
    }

}
