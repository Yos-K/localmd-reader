package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryListingTest {
    @Test
    void fromKeepsDirectoriesAndMarkdownDocumentsAsDistinctItemTypes() {
        MarkdownLibraryListing listing = MarkdownLibraryListing.from(Arrays.asList(
                FolderDocumentEntry.unsupportedFile("cover.png", "content://tree/cover"),
                FolderDocumentEntry.markdownFile("readme.md", "content://tree/readme"),
                FolderDocumentEntry.directory("notes", "content://tree/notes")));

        TestAssertions.assertEquals(2, listing.items().size(),
                "project library must omit unsupported files without losing navigable directories");
        TestAssertions.assertTrue(listing.items().get(0) instanceof MarkdownLibraryItem.DirectoryItem,
                "a directory must remain a directory item instead of becoming an openable document");
        TestAssertions.assertTrue(listing.items().get(1) instanceof MarkdownLibraryItem.DocumentItem,
                "a Markdown file must become an openable document item");
    }

    @Test
    void fromSortsDirectoriesBeforeDocumentsAndNamesWithinEachType() {
        MarkdownLibraryListing listing = MarkdownLibraryListing.from(Arrays.asList(
                FolderDocumentEntry.markdownFile("beta.md", "content://tree/beta"),
                FolderDocumentEntry.directory("Zulu", "content://tree/zulu"),
                FolderDocumentEntry.markdownFile("Alpha.md", "content://tree/alpha"),
                FolderDocumentEntry.directory("archive", "content://tree/archive")));

        TestAssertions.assertEquals("archive", listing.items().get(0).displayName(),
                "directories must be sorted by display name before documents");
        TestAssertions.assertEquals("Zulu", listing.items().get(1).displayName(),
                "all directories must remain grouped before documents");
        TestAssertions.assertEquals("Alpha.md", listing.items().get(2).displayName(),
                "Markdown documents must be sorted case-insensitively");
        TestAssertions.assertEquals("beta.md", listing.items().get(3).displayName(),
                "Markdown document ordering must be stable");
    }

    @Test
    void fromDeduplicatesEntriesByUriKeepingTheFirstTypedEntry() {
        MarkdownLibraryListing listing = MarkdownLibraryListing.from(Arrays.asList(
                FolderDocumentEntry.directory("current", "content://tree/same"),
                FolderDocumentEntry.markdownFile("old.md", "content://tree/same"),
                FolderDocumentEntry.markdownFile("other.md", "content://tree/other")));

        TestAssertions.assertEquals(2, listing.items().size(),
                "project library must not expose duplicate destinations");
        TestAssertions.assertTrue(listing.items().get(0) instanceof MarkdownLibraryItem.DirectoryItem,
                "the first typed entry for a duplicate URI must win");
        TestAssertions.assertEquals("current", listing.items().get(0).displayName(),
                "deduplication must preserve the first entry identity");
    }

    @Test
    void matchingKeepsANameMatchedDirectoryAsANavigableItem() {
        MarkdownLibraryListing listing = MarkdownLibraryListing.from(Arrays.asList(
                FolderDocumentEntry.directory("Guides", "content://tree/guides"),
                FolderDocumentEntry.markdownFile("README.md", "content://tree/readme")));

        MarkdownLibraryListing matching = listing.matching(MarkdownLibraryQuery.from("guide"));

        TestAssertions.assertTrue(matching.items().get(0) instanceof MarkdownLibraryItem.DirectoryItem,
                "filtering must preserve a matched directory's navigation type");
    }

    @Test
    void matchingOmitsEveryUnrelatedLibraryItem() {
        MarkdownLibraryListing listing = MarkdownLibraryListing.from(Arrays.asList(
                FolderDocumentEntry.directory("Guides", "content://tree/guides"),
                FolderDocumentEntry.markdownFile("README.md", "content://tree/readme")));

        MarkdownLibraryListing matching = listing.matching(MarkdownLibraryQuery.from("missing"));

        TestAssertions.assertEquals(0, matching.items().size(),
                "filtering must expose an explicit empty result when no current item matches");
    }

    @Test
    void blankQueryKeepsTheCompleteCurrentDirectory() {
        MarkdownLibraryListing listing = MarkdownLibraryListing.from(Arrays.asList(
                FolderDocumentEntry.directory("Guides", "content://tree/guides"),
                FolderDocumentEntry.markdownFile("README.md", "content://tree/readme")));

        MarkdownLibraryListing matching = listing.matching(MarkdownLibraryQuery.from(null));

        TestAssertions.assertEquals(2, matching.items().size(),
                "clearing the filter must restore every item without another provider query");
    }
}
