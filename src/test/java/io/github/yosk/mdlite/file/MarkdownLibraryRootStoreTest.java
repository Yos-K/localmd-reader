package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryRootStoreTest {
    @Test
    void loadConvertsStoredTreeUriToSelectedLibrary() {
        FakeRootStorage storage = new FakeRootStorage("content://tree/project");
        MarkdownLibraryRootStore store = new MarkdownLibraryRootStore(storage);

        RememberedMarkdownLibrary remembered = store.load();

        TestAssertions.assertTrue(remembered instanceof RememberedMarkdownLibrary.SelectedLibrary,
                "stored tree URI must restore a selected project library");
        TestAssertions.assertEquals("content://tree/project",
                ((RememberedMarkdownLibrary.SelectedLibrary) remembered).treeUri(),
                "restored project library must preserve the stored tree URI");
    }

    @Test
    void rememberWritesOnlyTheValidatedSelectedTreeUri() {
        FakeRootStorage storage = new FakeRootStorage("");
        MarkdownLibraryRootStore store = new MarkdownLibraryRootStore(storage);

        store.remember(RememberedMarkdownLibrary.selected("content://tree/project"));

        TestAssertions.assertEquals("content://tree/project", storage.treeUri,
                "remembering a project library must persist its validated tree URI");
    }

    @Test
    void forgetClearsThePersistedProjectLibrary() {
        FakeRootStorage storage = new FakeRootStorage("content://tree/project");
        MarkdownLibraryRootStore store = new MarkdownLibraryRootStore(storage);

        store.forget();

        TestAssertions.assertEquals("", storage.treeUri,
                "forgetting an inaccessible project library must clear its persisted URI");
    }

    private static final class FakeRootStorage implements MarkdownLibraryRootStorage {
        private String treeUri;

        private FakeRootStorage(String treeUri) {
            this.treeUri = treeUri;
        }

        @Override
        public String loadTreeUri() {
            return treeUri;
        }

        @Override
        public void saveTreeUri(String treeUri) {
            this.treeUri = treeUri;
        }

        @Override
        public void clearTreeUri() {
            treeUri = "";
        }
    }
}
