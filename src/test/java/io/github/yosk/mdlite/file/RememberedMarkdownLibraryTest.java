package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class RememberedMarkdownLibraryTest {
    @Test
    void missingStoredUriRestoresNoRememberedLibrary() {
        RememberedMarkdownLibrary remembered = RememberedMarkdownLibrary.fromStoredValue(null);

        TestAssertions.assertTrue(remembered instanceof RememberedMarkdownLibrary.NoRememberedLibrary,
                "missing persistence must not create a restorable project library");
    }

    @Test
    void blankStoredUriRestoresNoRememberedLibrary() {
        RememberedMarkdownLibrary remembered = RememberedMarkdownLibrary.fromStoredValue("   ");

        TestAssertions.assertTrue(remembered instanceof RememberedMarkdownLibrary.NoRememberedLibrary,
                "blank persistence must not create a restorable project library");
    }

    @Test
    void selectedTreeUriCreatesRestorableProjectLibrary() {
        RememberedMarkdownLibrary remembered =
                RememberedMarkdownLibrary.selected(" content://tree/project ");

        TestAssertions.assertTrue(remembered instanceof RememberedMarkdownLibrary.SelectedLibrary,
                "a selected tree URI must create a restorable project library");
        TestAssertions.assertEquals("content://tree/project",
                ((RememberedMarkdownLibrary.SelectedLibrary) remembered).treeUri(),
                "restorable project library must store a normalized tree URI");
    }

    @Test
    void selectedLibraryRejectsEmptyTreeUriBeforePersistence() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override
                    public void run() {
                        RememberedMarkdownLibrary.selected("");
                    }
                });
    }
}
