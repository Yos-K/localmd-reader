package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class DocumentSearchSessionTest {

    @Test
    void emptySessionHasNoActiveQuery() {
        DocumentSearchSession session = DocumentSearchSession.empty();

        TestAssertions.assertFalse(session.hasActiveQuery(), "empty search session must not navigate search results");
    }

    @Test
    void searchingReadableQueryStoresActiveQuery() {
        DocumentSearchSession session = DocumentSearchSession.empty().search(DocumentSearchQuery.from(" heading "));

        TestAssertions.assertTrue(session.hasActiveQuery(), "readable search query must be retained for next and previous actions");
        TestAssertions.assertEquals("heading", session.queryText(), "search session must expose the normalized query text");
    }

    @Test
    void searchingBlankQueryClearsActiveQuery() {
        DocumentSearchSession session = DocumentSearchSession.empty()
                .search(DocumentSearchQuery.from("markdown"))
                .search(DocumentSearchQuery.from("   "));

        TestAssertions.assertFalse(session.hasActiveQuery(), "blank search query must clear the active search session");
    }

    @Test
    void clearedSessionHasNoActiveQuery() {
        DocumentSearchSession session = DocumentSearchSession.empty()
                .search(DocumentSearchQuery.from("markdown"))
                .clear();

        TestAssertions.assertFalse(session.hasActiveQuery(), "cleared search session must not navigate stale results");
    }
}
