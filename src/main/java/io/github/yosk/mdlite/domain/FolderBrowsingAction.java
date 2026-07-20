package io.github.yosk.mdlite.domain;

public abstract class FolderBrowsingAction {
    private static final ChooseFromFolder CHOOSE_FROM_FOLDER = new ChooseFromFolder();
    private static final OpenProjectLibrary OPEN_PROJECT_LIBRARY = new OpenProjectLibrary();

    private FolderBrowsingAction() {
    }

    static ChooseFromFolder chooseFromFolder() {
        return CHOOSE_FROM_FOLDER;
    }

    static OpenProjectLibrary openProjectLibrary() {
        return OPEN_PROJECT_LIBRARY;
    }

    public abstract boolean closesMenuBeforeOpening();

    public abstract boolean hasExpandableMenuTree();

    public static final class ChooseFromFolder extends FolderBrowsingAction {
        private ChooseFromFolder() {
        }

        @Override
        public boolean closesMenuBeforeOpening() {
            return true;
        }

        @Override
        public boolean hasExpandableMenuTree() {
            return false;
        }
    }

    public static final class OpenProjectLibrary extends FolderBrowsingAction {
        private OpenProjectLibrary() {
        }

        @Override
        public boolean closesMenuBeforeOpening() {
            return false;
        }

        @Override
        public boolean hasExpandableMenuTree() {
            return true;
        }
    }
}
