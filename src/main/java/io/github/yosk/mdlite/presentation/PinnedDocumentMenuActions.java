package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.viewer.PinnedDocumentMenuVisibility;

final class PinnedDocumentMenuActions {
    private PinnedDocumentMenuActions() {
    }

    static MainMenuAction pinCurrentFile() {
        return new PinCurrentFileMenuAction();
    }

    static MainMenuAction pinnedFiles() {
        return new PinnedFilesMenuAction();
    }

    static MainMenuAction unpinCurrentFile() {
        return new UnpinCurrentFileMenuAction();
    }

    private static final class PinCurrentFileMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().pinCurrentFile();
        }

        @Override
        boolean visible(MainActivity activity) {
            return visibility(activity).canPinCurrentFile();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.pinCurrentDocument();
        }
    }

    private static final class PinnedFilesMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().pinnedFiles();
        }

        @Override
        boolean visible(MainActivity activity) {
            return visibility(activity).canOpenPinnedFiles();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showPinnedDocuments();
        }
    }

    private static final class UnpinCurrentFileMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().unpinCurrentFile();
        }

        @Override
        boolean visible(MainActivity activity) {
            return visibility(activity).canUnpinCurrentFile();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.unpinCurrentDocument();
        }
    }

    private static PinnedDocumentMenuVisibility visibility(MainActivity activity) {
        return PinnedDocumentMenuVisibility.of(
                activity.pinnedDocumentsAvailable(),
                activity.activeTabIsFile(),
                activity.activeFileIsPinned());
    }
}
