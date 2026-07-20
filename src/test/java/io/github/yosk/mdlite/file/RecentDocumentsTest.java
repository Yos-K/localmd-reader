package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public final class RecentDocumentsTest {

    @Test
    void recordOpenedAddsNewestDocumentToTop() {
        RecentDocuments documents = RecentDocuments.empty(5)
                .recordOpened(doc("first.md", "content://first"))
                .recordOpened(doc("second.md", "content://second"));

        TestAssertions.assertEquals("second.md", documents.items().get(0).displayName(), "newest opened document must be first");
        TestAssertions.assertEquals("first.md", documents.items().get(1).displayName(), "older document must remain after newest");
    }

    @Test
    void recordOpenedMovesExistingDocumentToTopWithoutDuplicate() {
        RecentDocuments documents = RecentDocuments.empty(5)
                .recordOpened(doc("first.md", "content://first"))
                .recordOpened(doc("second.md", "content://second"))
                .recordOpened(doc("first renamed.md", "content://first"));

        TestAssertions.assertEquals(2, documents.items().size(), "reopening the same URI must not create a duplicate recent entry");
        TestAssertions.assertEquals("first renamed.md", documents.items().get(0).displayName(), "reopened document must move to top with latest display name");
        TestAssertions.assertEquals("second.md", documents.items().get(1).displayName(), "other recent document must keep relative order");
    }

    @Test
    void recordOpenedKeepsOnlyTheConfiguredMaximumNumberOfDocuments() {
        RecentDocuments documents = RecentDocuments.empty(3)
                .recordOpened(doc("one.md", "content://one"))
                .recordOpened(doc("two.md", "content://two"))
                .recordOpened(doc("three.md", "content://three"))
                .recordOpened(doc("four.md", "content://four"));

        TestAssertions.assertEquals(3, documents.items().size(), "recent documents must be limited to the configured maximum");
        TestAssertions.assertEquals("four.md", documents.items().get(0).displayName(), "newest document must be kept");
        TestAssertions.assertEquals("two.md", documents.items().get(2).displayName(), "oldest document beyond the limit must be dropped");
    }

    @Test
    void fromRestoresStoredDocumentsWithoutDuplicateUris() {
        RecentDocuments documents = RecentDocuments.from(5, Arrays.asList(
                doc("new.md", "content://same"),
                doc("old duplicate.md", "content://same"),
                doc("other.md", "content://other")));

        TestAssertions.assertEquals(2, documents.items().size(), "restored recent documents must drop duplicate URIs");
        TestAssertions.assertEquals("new.md", documents.items().get(0).displayName(), "first stored duplicate must win because it is newest");
        TestAssertions.assertEquals("other.md", documents.items().get(1).displayName(), "non-duplicate stored document must remain");
    }

    @Test
    void documentUsesFallbackDisplayNameWhenNameIsBlank() {
        RecentDocument document = RecentDocument.of("  ", "content://blank-name");

        TestAssertions.assertEquals("Untitled Markdown", document.displayName(), "blank display name must not leak an empty recent item label");
    }

    @Test
    void clearRemovesEveryRecentDocument() {
        RecentDocuments documents = RecentDocuments.empty(5)
                .recordOpened(doc("first.md", "content://first"))
                .recordOpened(doc("second.md", "content://second"))
                .clear();

        TestAssertions.assertEquals(0, documents.items().size(), "clearing recent documents must remove every stored document");
    }

    private static RecentDocument doc(String displayName, String uri) {
        return RecentDocument.of(displayName, uri);
    }

}
