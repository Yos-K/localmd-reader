package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.domain.FolderBrowsingMode;

public abstract class MarkdownLibraryEntryPoint {
    private static final ChooseFolder CHOOSE_FOLDER = new ChooseFolder();

    private MarkdownLibraryEntryPoint() {
    }

    public static MarkdownLibraryEntryPoint from(FolderBrowsingMode mode,
            RememberedMarkdownLibrary rememberedLibrary) {
        if (mode == null || rememberedLibrary == null) {
            throw new IllegalArgumentException("folder mode and remembered library must not be null");
        }
        if (mode instanceof FolderBrowsingMode.ProjectFolderNavigation
                && rememberedLibrary instanceof RememberedMarkdownLibrary.SelectedLibrary) {
            RememberedMarkdownLibrary.SelectedLibrary selected =
                    (RememberedMarkdownLibrary.SelectedLibrary) rememberedLibrary;
            return new ResumeProjectLibrary(selected.treeUri());
        }
        return CHOOSE_FOLDER;
    }

    public static final class ChooseFolder extends MarkdownLibraryEntryPoint {
        private ChooseFolder() {
        }
    }

    public static final class ResumeProjectLibrary extends MarkdownLibraryEntryPoint {
        private final String treeUri;

        private ResumeProjectLibrary(String treeUri) {
            this.treeUri = treeUri;
        }

        public String treeUri() {
            return treeUri;
        }
    }
}
