package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.domain.FolderBrowsingAction;
import io.github.yosk.mdlite.domain.FolderBrowsingMode;

final class FolderBrowsingMenuActions {
    private FolderBrowsingMenuActions() {
    }

    static MainMenuAction openFolder() {
        return new OpenFolderMenuAction();
    }

    private static final class OpenFolderMenuAction extends MainMenuAction {
        @Override
        String label(MainActivity activity) {
            FolderBrowsingAction action = FolderBrowsingMode.from(activity.featureEntitlement).action();
            if (action.hasExpandableMenuTree()) {
                return activity.viewerText().openMarkdownLibrary();
            }
            return activity.viewerText().openFolder();
        }

        @Override
        void perform(MainActivity activity) {
            FolderBrowsingAction action = FolderBrowsingMode.from(activity.featureEntitlement).action();
            if (action instanceof FolderBrowsingAction.OpenProjectLibrary) {
                activity.toggleMarkdownLibraryTree();
                return;
            }
            if (action.closesMenuBeforeOpening()) {
                activity.closeMenu();
            }
            activity.openMarkdownLibrary();
        }
    }
}
