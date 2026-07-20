package io.github.yosk.mdlite.presentation;

final class PrintDocumentMenuActions {
    private PrintDocumentMenuActions() {
    }

    static MainMenuAction printOrSavePdf() {
        return new PrintOrSavePdfMenuAction();
    }

    private static final class PrintOrSavePdfMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().printOrSavePdf();
        }

        @Override
        boolean visible(MainActivity activity) {
            return activity.documentOutputAvailable();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.printActiveDocument();
        }
    }
}
