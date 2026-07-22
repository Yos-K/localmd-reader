package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryEntryPointTest {
    @Test
    void proFolderBrowsingChoosesFolderWhenNoProjectWasRemembered() {
        MarkdownLibraryEntryPoint entryPoint = MarkdownLibraryEntryPoint.from(
                RememberedMarkdownLibrary.none());

        TestAssertions.assertTrue(entryPoint instanceof MarkdownLibraryEntryPoint.ChooseFolder,
                "Pro folder browsing must request a root when no project library was selected before");
    }

    @Test
    void proFolderBrowsingRestoresTheRememberedProjectLibrary() {
        MarkdownLibraryEntryPoint entryPoint = MarkdownLibraryEntryPoint.from(
                RememberedMarkdownLibrary.selected("content://tree/project"));

        TestAssertions.assertTrue(entryPoint instanceof MarkdownLibraryEntryPoint.ResumeProjectLibrary,
                "Pro folder browsing must resume a previously selected project library");
        TestAssertions.assertEquals("content://tree/project",
                ((MarkdownLibraryEntryPoint.ResumeProjectLibrary) entryPoint).treeUri(),
                "project-library resume must preserve its permission-bearing tree URI");
    }
}
