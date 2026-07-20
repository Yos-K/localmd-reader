package io.github.yosk.mdlite.presentation;

final class MainMenuActions {
    private MainMenuActions() {
    }

    static MainMenuAction openFile() {
        return new OpenFileMenuAction();
    }

    static MainMenuAction openFolder() {
        return FolderBrowsingMenuActions.openFolder();
    }

    static MainMenuAction createFromClipboard() {
        return new CreateFromClipboardMenuAction();
    }

    static MainMenuAction saveAs() {
        return new SaveAsMenuAction();
    }

    static MainMenuAction exportAsHtml() {
        return HtmlExportMenuActions.exportAsHtml();
    }

    static MainMenuAction pinCurrentFile() {
        return PinnedDocumentMenuActions.pinCurrentFile();
    }

    static MainMenuAction unpinCurrentFile() {
        return PinnedDocumentMenuActions.unpinCurrentFile();
    }

    static MainMenuAction pinnedFiles() {
        return PinnedDocumentMenuActions.pinnedFiles();
    }

    static MainMenuAction recentFiles() {
        return new RecentFilesMenuAction();
    }

    static MainMenuAction settings() {
        return new SettingsMenuAction();
    }

    static MainMenuAction tableOfContents() {
        return new TableOfContentsMenuAction();
    }

    static MainMenuAction findInDocument() {
        return new FindInDocumentMenuAction();
    }

    static MainMenuAction theme() {
        return new ThemeMenuAction();
    }

    static MainMenuAction language() {
        return new LanguageMenuAction();
    }

    static MainMenuAction controlsPlacement() {
        return new ControlsPlacementMenuAction();
    }

    static MainMenuAction gestureShortcuts() {
        return new GestureShortcutsMenuAction();
    }

    static MainMenuAction proFeatures() {
        return new ProFeaturesMenuAction();
    }

    static MainMenuAction clipboardDiagnostics() {
        return new ClipboardDiagnosticsMenuAction();
    }

    static MainMenuAction privacy() {
        return new PrivacyMenuAction();
    }

    private static final class OpenFileMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().openFile();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.openMarkdownPicker();
        }
    }

    private static final class CreateFromClipboardMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().createFromClipboard();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.createMarkdownFromClipboard();
        }
    }

    private static final class SaveAsMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().saveAs();
        }

        @Override
        boolean visible(MainActivity activity) {
            return activity.activeTabIsDraft();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.saveActiveMarkdownAs();
        }
    }

    private static final class RecentFilesMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.recentFilesTitle();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showRecentDocuments();
        }
    }

    private static final class SettingsMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().settings();
        }

        @Override
        void perform(MainActivity activity) {
            activity.toggleSettingsPanel();
        }
    }

    private static final class TableOfContentsMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().tableOfContents();
        }

        @Override
        boolean visible(MainActivity activity) {
            return activity.tableOfContentsAvailable();
        }

        @Override
        void perform(MainActivity activity) {
            activity.toggleTableOfContentsPanel();
        }
    }

    private static final class FindInDocumentMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().findInDocument();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showFindInDocumentDialog();
        }
    }

    private static final class ThemeMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().themeLabel(activity.currentTheme());
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showThemeDialog();
        }
    }

    private static final class LanguageMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().switchLanguage();
        }

        @Override
        void perform(MainActivity activity) {
            activity.switchLanguage();
            activity.closeMenu();
        }
    }

    private static final class ControlsPlacementMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            if (activity.controlsPlacement().isBottom()) {
                return activity.viewerText().moveControlsToTop();
            }
            return activity.viewerText().moveControlsToBottom();
        }

        @Override
        void perform(MainActivity activity) {
            activity.toggleControlsPlacement();
            activity.closeMenu();
        }
    }

    private static final class GestureShortcutsMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().gestures();
        }

        @Override
        boolean visible(MainActivity activity) {
            return activity.customGestureShortcutsAvailable();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showGestureShortcutsDialog();
        }
    }

    private static final class ProFeaturesMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().proFeatures();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showProFeaturesDialog();
        }
    }

    private static final class ClipboardDiagnosticsMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().clipboardDiagnostics();
        }

        @Override
        boolean visible(MainActivity activity) {
            return false;
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showClipboardDiagnosticsDialog();
        }
    }

    private static final class PrivacyMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().privacy();
        }

        @Override
        void perform(MainActivity activity) {
            activity.closeMenu();
            activity.showPrivacyPolicyDialog();
        }
    }
}
