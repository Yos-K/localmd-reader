package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.FolderBrowsingMode;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryEntryPointTest {
    @Test
    void freeFolderBrowsingAlwaysChoosesFolderEvenWhenAProjectWasRemembered() {
        MarkdownLibraryEntryPoint entryPoint = MarkdownLibraryEntryPoint.from(
                FolderBrowsingMode.from(FeatureEntitlement.free()),
                RememberedMarkdownLibrary.selected("content://tree/project"));

        TestAssertions.assertTrue(entryPoint instanceof MarkdownLibraryEntryPoint.ChooseFolder,
                "Free folder browsing must preserve the existing Android folder selection flow");
    }

    @Test
    void proFolderBrowsingChoosesFolderWhenNoProjectWasRemembered() {
        MarkdownLibraryEntryPoint entryPoint = MarkdownLibraryEntryPoint.from(
                FolderBrowsingMode.from(FeatureEntitlement.pro()),
                RememberedMarkdownLibrary.none());

        TestAssertions.assertTrue(entryPoint instanceof MarkdownLibraryEntryPoint.ChooseFolder,
                "Pro folder browsing must request a root when no project library was selected before");
    }

    @Test
    void proFolderBrowsingRestoresTheRememberedProjectLibrary() {
        MarkdownLibraryEntryPoint entryPoint = MarkdownLibraryEntryPoint.from(
                FolderBrowsingMode.from(FeatureEntitlement.pro()),
                RememberedMarkdownLibrary.selected("content://tree/project"));

        TestAssertions.assertTrue(entryPoint instanceof MarkdownLibraryEntryPoint.ResumeProjectLibrary,
                "Pro folder browsing must resume a previously selected project library");
        TestAssertions.assertEquals("content://tree/project",
                ((MarkdownLibraryEntryPoint.ResumeProjectLibrary) entryPoint).treeUri(),
                "project-library resume must preserve its permission-bearing tree URI");
    }
}
