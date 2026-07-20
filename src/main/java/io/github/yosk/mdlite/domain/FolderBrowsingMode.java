package io.github.yosk.mdlite.domain;

public abstract class FolderBrowsingMode {
    private FolderBrowsingMode() {
    }

    public static FolderBrowsingMode from(FeatureEntitlement entitlement) {
        if (entitlement != null && entitlement.allows(ViewerFeature.PROJECT_LIBRARY)) {
            return new ProjectFolderNavigation();
        }
        return new FlatFolderSelection();
    }

    public abstract FolderBrowsingAction action();

    public static final class FlatFolderSelection extends FolderBrowsingMode {
        private FlatFolderSelection() {
        }

        @Override
        public FolderBrowsingAction.ChooseFromFolder action() {
            return FolderBrowsingAction.chooseFromFolder();
        }
    }

    public static final class ProjectFolderNavigation extends FolderBrowsingMode {
        private ProjectFolderNavigation() {
        }

        @Override
        public FolderBrowsingAction.OpenProjectLibrary action() {
            return FolderBrowsingAction.openProjectLibrary();
        }
    }
}
