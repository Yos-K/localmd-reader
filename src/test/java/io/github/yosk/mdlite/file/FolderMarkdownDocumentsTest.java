package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public final class FolderMarkdownDocumentsTest {
    @Test
    void fromKeepsMarkdownFilesAndDropsDirectoriesAndUnsupportedFiles() {
        FolderMarkdownDocuments documents = FolderMarkdownDocuments.from(Arrays.asList(
                FolderDocumentEntry.directory("notes", "content://tree/notes"),
                FolderDocumentEntry.markdownFile("readme.md", "content://tree/readme"),
                FolderDocumentEntry.markdownFile("plan.markdown", "content://tree/plan"),
                FolderDocumentEntry.unsupportedFile("image.png", "content://tree/image")));

        TestAssertions.assertEquals(2, documents.items().size(),
                "folder browsing must show only Markdown files as openable documents");
        TestAssertions.assertEquals("plan.markdown", documents.items().get(0).displayName(),
                "Markdown documents must be sorted by display name");
        TestAssertions.assertEquals("readme.md", documents.items().get(1).displayName(),
                "Markdown documents must keep a stable sorted order");
    }

    @Test
    void fromDeduplicatesMarkdownFilesByUriKeepingTheFirstEntry() {
        FolderMarkdownDocuments documents = FolderMarkdownDocuments.from(Arrays.asList(
                FolderDocumentEntry.markdownFile("current.md", "content://tree/same"),
                FolderDocumentEntry.markdownFile("old-name.md", "content://tree/same"),
                FolderDocumentEntry.markdownFile("other.md", "content://tree/other")));

        TestAssertions.assertEquals(2, documents.items().size(),
                "folder browsing must not show duplicate entries for the same URI");
        TestAssertions.assertEquals("current.md", documents.items().get(0).displayName(),
                "the first entry for a URI must win when folder contents contain duplicates");
        TestAssertions.assertEquals("other.md", documents.items().get(1).displayName(),
                "non-duplicate Markdown documents must remain openable");
    }

    @Test
    void markdownFileRejectsUnsupportedNamesBeforeTheyEnterTheOpenableFolderModel() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override
                    public void run() {
                        FolderDocumentEntry.markdownFile("image.png", "content://tree/image");
                    }
                });
    }

    @Test
    void markdownFileAcceptsUppercaseMarkdownExtensions() {
        FolderDocumentEntry entry = FolderDocumentEntry.markdownFile("README.MD", "content://tree/readme");

        TestAssertions.assertEquals("README.MD", entry.displayName(),
                "folder browsing must accept Markdown extensions case-insensitively");
    }
}
