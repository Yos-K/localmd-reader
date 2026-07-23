package io.github.yosk.mdlite.file;

public abstract class MarkdownLibraryEntryPoint {
    private static final ChooseFolder CHOOSE_FOLDER = new ChooseFolder();

    private MarkdownLibraryEntryPoint() {
    }

    public static MarkdownLibraryEntryPoint from(RememberedMarkdownLibrary rememberedLibrary) {
        if (rememberedLibrary == null) {
            throw new IllegalArgumentException("remembered library must not be null");
        }
        if (rememberedLibrary instanceof RememberedMarkdownLibrary.SelectedLibrary) {
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
