package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.file.RecentDocument;

public abstract class DocumentListCommand {
    private static final None NONE = new None();
    private static final ClearRecent CLEAR_RECENT = new ClearRecent();
    private static final ClearPinned CLEAR_PINNED = new ClearPinned();
    private static final ChooseAnotherFolder CHOOSE_ANOTHER_FOLDER = new ChooseAnotherFolder();

    private DocumentListCommand() {
    }

    static None none() {
        return NONE;
    }

    static OpenDocument open(RecentDocument document) {
        return new OpenDocument(document);
    }

    static ClearRecent clearRecent() {
        return CLEAR_RECENT;
    }

    static ClearPinned clearPinned() {
        return CLEAR_PINNED;
    }

    static ChooseAnotherFolder chooseAnotherFolder() {
        return CHOOSE_ANOTHER_FOLDER;
    }

    public abstract void execute(Handler handler);

    public interface Handler {
        void none();

        void openDocument(RecentDocument document);

        void clearRecent();

        void clearPinned();

        void chooseAnotherFolder();
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

    public static final class ChooseAnotherFolder extends DocumentListCommand {
        private ChooseAnotherFolder() {
        }

        @Override
        public void execute(Handler handler) {
            handler.chooseAnotherFolder();
        }
    }
}
