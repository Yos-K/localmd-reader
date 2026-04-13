package io.github.yosk.mdlite.domain;

import java.util.Arrays;

public final class RecentDocumentsTest {
    public static void main(String[] args) {
        RecentDocumentsTest test = new RecentDocumentsTest();
        test.recordOpenedAddsNewestDocumentToTop();
        test.recordOpenedMovesExistingDocumentToTopWithoutDuplicate();
        test.recordOpenedKeepsOnlyTheConfiguredMaximumNumberOfDocuments();
        test.fromRestoresStoredDocumentsWithoutDuplicateUris();
        test.documentUsesFallbackDisplayNameWhenNameIsBlank();
        test.clearRemovesEveryRecentDocument();
    }

    public void recordOpenedAddsNewestDocumentToTop() {
        RecentDocuments documents = RecentDocuments.empty(5)
                .recordOpened(doc("first.md", "content://first"))
                .recordOpened(doc("second.md", "content://second"));

        assertEquals("second.md", documents.items().get(0).displayName(), "newest opened document must be first");
        assertEquals("first.md", documents.items().get(1).displayName(), "older document must remain after newest");
    }

    public void recordOpenedMovesExistingDocumentToTopWithoutDuplicate() {
        RecentDocuments documents = RecentDocuments.empty(5)
                .recordOpened(doc("first.md", "content://first"))
                .recordOpened(doc("second.md", "content://second"))
                .recordOpened(doc("first renamed.md", "content://first"));

        assertEquals(2, documents.items().size(), "reopening the same URI must not create a duplicate recent entry");
        assertEquals("first renamed.md", documents.items().get(0).displayName(), "reopened document must move to top with latest display name");
        assertEquals("second.md", documents.items().get(1).displayName(), "other recent document must keep relative order");
    }

    public void recordOpenedKeepsOnlyTheConfiguredMaximumNumberOfDocuments() {
        RecentDocuments documents = RecentDocuments.empty(3)
                .recordOpened(doc("one.md", "content://one"))
                .recordOpened(doc("two.md", "content://two"))
                .recordOpened(doc("three.md", "content://three"))
                .recordOpened(doc("four.md", "content://four"));

        assertEquals(3, documents.items().size(), "recent documents must be limited to the configured maximum");
        assertEquals("four.md", documents.items().get(0).displayName(), "newest document must be kept");
        assertEquals("two.md", documents.items().get(2).displayName(), "oldest document beyond the limit must be dropped");
    }

    public void fromRestoresStoredDocumentsWithoutDuplicateUris() {
        RecentDocuments documents = RecentDocuments.from(5, Arrays.asList(
                doc("new.md", "content://same"),
                doc("old duplicate.md", "content://same"),
                doc("other.md", "content://other")));

        assertEquals(2, documents.items().size(), "restored recent documents must drop duplicate URIs");
        assertEquals("new.md", documents.items().get(0).displayName(), "first stored duplicate must win because it is newest");
        assertEquals("other.md", documents.items().get(1).displayName(), "non-duplicate stored document must remain");
    }

    public void documentUsesFallbackDisplayNameWhenNameIsBlank() {
        RecentDocument document = RecentDocument.of("  ", "content://blank-name");

        assertEquals("Untitled Markdown", document.displayName(), "blank display name must not leak an empty recent item label");
    }

    public void clearRemovesEveryRecentDocument() {
        RecentDocuments documents = RecentDocuments.empty(5)
                .recordOpened(doc("first.md", "content://first"))
                .recordOpened(doc("second.md", "content://second"))
                .clear();

        assertEquals(0, documents.items().size(), "clearing recent documents must remove every stored document");
    }

    private static RecentDocument doc(String displayName, String uri) {
        return RecentDocument.of(displayName, uri);
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }
}
