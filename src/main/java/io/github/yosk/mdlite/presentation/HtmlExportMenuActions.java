package io.github.yosk.mdlite.presentation;

final class HtmlExportMenuActions {
    private HtmlExportMenuActions() {
    }

    static MainMenuAction exportAsHtml() {
        return new ExportAsHtmlMenuAction();
    }

    private static final class ExportAsHtmlMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().exportAsHtml();
        }

        @Override
        boolean visible(MainActivity activity) {
            return activity.documentOutputAvailable();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.exportActiveDocumentAsHtml();
        }
    }
}
