package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.domain.ViewerFeature;

final class MarkdownLibraryMenuActions {
    private MarkdownLibraryMenuActions() {
    }

    static MainMenuAction markdownLibrary() {
        return new MarkdownLibraryMenuAction();
    }

    private static final class MarkdownLibraryMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            return activity.viewerText().openMarkdownLibrary();
        }

        @Override
        void perform(MainActivity activity) {
            activity.toggleMarkdownLibraryTree();
        }

        @Override
        boolean visible(MainActivity activity) {
            return activity.featureEntitlement.allows(ViewerFeature.PROJECT_LIBRARY);
        }
    }
}
