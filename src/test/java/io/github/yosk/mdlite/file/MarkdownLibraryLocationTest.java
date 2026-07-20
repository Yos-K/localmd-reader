package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryLocationTest {
    @Test
    void rootLocationBackRemainsAtTheAlwaysValidRoot() {
        MarkdownLibraryLocation.RootLocation root =
                MarkdownLibraryLocation.root("content://tree/project", "Project");

        TestAssertions.assertSame(root, root.back(),
                "back at the project root must remain a valid root location");
        TestAssertions.assertEquals("content://tree/project", root.treeUri(),
                "root location must retain the permission-bearing tree URI");
    }

    @Test
    void enteringDirectoryCreatesNestedLocationWithItsParent() {
        MarkdownLibraryLocation.RootLocation root =
                MarkdownLibraryLocation.root("content://tree/project", "Project");

        MarkdownLibraryLocation.NestedLocation notes = root.enter(
                MarkdownLibraryItem.directory("notes", "content://tree/project/notes"));

        TestAssertions.assertEquals("notes", notes.displayName(),
                "entered directory must become the current project location");
        TestAssertions.assertEquals("content://tree/project/notes", notes.directoryUri(),
                "nested location must retain its document URI");
        TestAssertions.assertSame(root, notes.back(),
                "back from the first nested directory must restore the root location");
    }

    @Test
    void nestedLocationCanEnterAndReturnAcrossMultipleLevels() {
        MarkdownLibraryLocation.RootLocation root =
                MarkdownLibraryLocation.root("content://tree/project", "Project");
        MarkdownLibraryLocation.NestedLocation notes = root.enter(
                MarkdownLibraryItem.directory("notes", "content://tree/project/notes"));

        MarkdownLibraryLocation.NestedLocation archive = notes.enter(
                MarkdownLibraryItem.directory("archive", "content://tree/project/notes/archive"));

        TestAssertions.assertSame(notes, archive.back(),
                "back from a deep directory must restore its exact parent location");
        TestAssertions.assertEquals("content://tree/project", archive.treeUri(),
                "all nested locations must preserve the original tree permission URI");
    }
}
