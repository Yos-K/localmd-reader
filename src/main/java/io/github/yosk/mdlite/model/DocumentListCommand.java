package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.file.RecentDocument;

public abstract class DocumentListCommand {
    private static final None NONE = new None();
    private static final ClearRecent CLEAR_RECENT = new ClearRecent();
    private static final ClearPinned CLEAR_PINNED = new ClearPinned();

    private DocumentListCommand() {
    }

    static None none() {
        return NONE;
    }

    static OpenDocument open(RecentDocument document) {
        return new OpenDocument(document);
    }

    static ChoosePinnedDocumentAction choosePinnedDocumentAction(RecentDocument document) {
        return new ChoosePinnedDocumentAction(document);
    }

    static UnpinDocument unpin(RecentDocument document) {
        return new UnpinDocument(document);
    }

    static ClearRecent clearRecent() {
        return CLEAR_RECENT;
    }

    static ClearPinned clearPinned() {
        return CLEAR_PINNED;
    }

    public abstract void execute(Handler handler);

    public interface Handler {
        void none();

        void openDocument(RecentDocument document);

        void choosePinnedDocumentAction(RecentDocument document);

        void unpinDocument(RecentDocument document);

        void clearRecent();

        void clearPinned();

    }

    public static final class None extends DocumentListCommand {
        private None() {
        }

        @Override
        public void execute(Handler handler) {
            handler.none();
        }
    }

    public static final class OpenDocument extends DocumentListCommand {
        private final RecentDocument document;

        private OpenDocument(RecentDocument document) {
            if (document == null) {
                throw new IllegalArgumentException("document-list open command requires a document");
            }
            this.document = document;
        }

        public RecentDocument document() {
            return document;
        }

        @Override
        public void execute(Handler handler) {
            handler.openDocument(document);
        }
    }

    public static final class ChoosePinnedDocumentAction extends DocumentListCommand {
        private final RecentDocument document;

        private ChoosePinnedDocumentAction(RecentDocument document) {
            if (document == null) {
                throw new IllegalArgumentException("pinned-document action requires a document");
            }
            this.document = document;
        }

        @Override
        public void execute(Handler handler) {
            handler.choosePinnedDocumentAction(document);
        }
    }

    public static final class UnpinDocument extends DocumentListCommand {
        private final RecentDocument document;

        private UnpinDocument(RecentDocument document) {
            if (document == null) {
                throw new IllegalArgumentException("unpin command requires a document");
            }
            this.document = document;
        }

        @Override
        public void execute(Handler handler) {
            handler.unpinDocument(document);
        }
    }

    public static final class ClearRecent extends DocumentListCommand {
        private ClearRecent() {
        }

        @Override
        public void execute(Handler handler) {
            handler.clearRecent();
        }
    }

    public static final class ClearPinned extends DocumentListCommand {
        private ClearPinned() {
        }

        @Override
        public void execute(Handler handler) {
            handler.clearPinned();
        }
    }

}
