package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public final class PinnedDocumentsTest {
    @Test
    void pinAddsPinnedDocumentToTop() {
        PinnedDocuments documents = PinnedDocuments.empty(5)
                .pin(doc("first.md", "content://first"))
                .pin(doc("second.md", "content://second"));

        TestAssertions.assertEquals("second.md", documents.items().get(0).displayName(),
                "newly pinned document must be first");
        TestAssertions.assertEquals("first.md", documents.items().get(1).displayName(),
                "older pinned document must remain after the newest pin");
    }

    @Test
    void pinMovesExistingPinnedDocumentToTopWithoutDuplicate() {
        PinnedDocuments documents = PinnedDocuments.empty(5)
                .pin(doc("first.md", "content://first"))
                .pin(doc("second.md", "content://second"))
                .pin(doc("first renamed.md", "content://first"));

        TestAssertions.assertEquals(2, documents.items().size(),
                "pinning the same URI must not duplicate pinned documents");
        TestAssertions.assertEquals("first renamed.md", documents.items().get(0).displayName(),
                "repinned document must move to top with the latest display name");
        TestAssertions.assertEquals("second.md", documents.items().get(1).displayName(),
                "other pinned documents must keep relative order");
    }

    @Test
    void pinKeepsOnlyTheConfiguredMaximumNumberOfDocuments() {
        PinnedDocuments documents = PinnedDocuments.empty(3)
                .pin(doc("one.md", "content://one"))
                .pin(doc("two.md", "content://two"))
                .pin(doc("three.md", "content://three"))
                .pin(doc("four.md", "content://four"));

        TestAssertions.assertEquals(3, documents.items().size(),
                "pinned documents must be limited to the configured maximum");
        TestAssertions.assertEquals("four.md", documents.items().get(0).displayName(),
                "newest pinned document must be kept");
        TestAssertions.assertEquals("two.md", documents.items().get(2).displayName(),
                "oldest pinned document beyond the limit must be dropped");
    }

    @Test
    void unpinRemovesPinnedDocumentByUri() {
        PinnedDocuments documents = PinnedDocuments.empty(5)
                .pin(doc("first.md", "content://first"))
                .pin(doc("second.md", "content://second"))
                .unpin("content://first");

        TestAssertions.assertEquals(1, documents.items().size(),
                "unpinning must remove the matching pinned document");
        TestAssertions.assertEquals("second.md", documents.items().get(0).displayName(),
                "unpinning must keep non-matching pinned documents");
    }

    @Test
    void containsUriReturnsTrueOnlyForPinnedDocumentUri() {
        PinnedDocuments documents = PinnedDocuments.empty(5)
                .pin(doc("first.md", "content://first"));

        TestAssertions.assertTrue(documents.containsUri("content://first"),
                "pinned documents must identify a URI that is currently pinned");
        TestAssertions.assertFalse(documents.containsUri("content://other"),
                "pinned documents must reject a URI that is not currently pinned");
        TestAssertions.assertFalse(documents.containsUri(null),
                "pinned documents must reject a missing URI");
    }

    @Test
    void fromRestoresStoredPinnedDocumentsWithoutDuplicateUris() {
        PinnedDocuments documents = PinnedDocuments.from(5, Arrays.asList(
                doc("new.md", "content://same"),
                doc("old duplicate.md", "content://same"),
                doc("other.md", "content://other")));

        TestAssertions.assertEquals(2, documents.items().size(),
                "restored pinned documents must drop duplicate URIs");
        TestAssertions.assertEquals("new.md", documents.items().get(0).displayName(),
                "first stored duplicate must win because it is the most recent pin");
        TestAssertions.assertEquals("other.md", documents.items().get(1).displayName(),
                "non-duplicate pinned document must remain");
    }

    private static RecentDocument doc(String displayName, String uri) {
        return RecentDocument.of(displayName, uri);
    }
}
