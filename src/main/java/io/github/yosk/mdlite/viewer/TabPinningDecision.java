package io.github.yosk.mdlite.viewer;

public abstract class TabPinningDecision {
    private TabPinningDecision() {
    }

    public static TabPinningDecision from(
            boolean pinningAvailable,
            OpenDocumentTab tab,
            boolean pinned) {
        if (!pinningAvailable || !(tab instanceof OpenDocumentTab.FileDocumentTab)) {
            return new Unavailable();
        }
        if (pinned) {
            return new Unpin((OpenDocumentTab.FileDocumentTab) tab);
        }
        return new Pin((OpenDocumentTab.FileDocumentTab) tab);
    }

    public abstract String tabDescription(ViewerText text, String title);

    public abstract boolean perform(Handler handler);

    public interface Handler {
        void pin(OpenDocumentTab.FileDocumentTab tab);

        void unpin(OpenDocumentTab.FileDocumentTab tab);
    }

    public static final class Unavailable extends TabPinningDecision {
        private Unavailable() {
        }

        @Override
        public String tabDescription(ViewerText text, String title) {
            return text.documentTab(title);
        }

        @Override
        public boolean perform(Handler handler) {
            return false;
        }
    }

    public static final class Pin extends TabPinningDecision {
        private final OpenDocumentTab.FileDocumentTab tab;

        private Pin(OpenDocumentTab.FileDocumentTab tab) {
            this.tab = tab;
        }

        public OpenDocumentTab.FileDocumentTab tab() {
            return tab;
        }

        @Override
        public String tabDescription(ViewerText text, String title) {
            return text.documentTab(title);
        }

        @Override
        public boolean perform(Handler handler) {
            handler.pin(tab);
            return true;
        }
    }

    public static final class Unpin extends TabPinningDecision {
        private final OpenDocumentTab.FileDocumentTab tab;

        private Unpin(OpenDocumentTab.FileDocumentTab tab) {
            this.tab = tab;
        }

        public OpenDocumentTab.FileDocumentTab tab() {
            return tab;
        }

        @Override
        public String tabDescription(ViewerText text, String title) {
            return text.pinnedDocumentTab(title);
        }

        @Override
        public boolean perform(Handler handler) {
            handler.unpin(tab);
            return true;
        }
    }
}
