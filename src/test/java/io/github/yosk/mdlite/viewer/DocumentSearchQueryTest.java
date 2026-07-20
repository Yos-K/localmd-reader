package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class DocumentSearchQueryTest {

    @Test
    void fromTrimsReadableQueryText() {
        DocumentSearchQuery query = DocumentSearchQuery.from("  heading  ");

        TestAssertions.assertEquals("heading", query.text(), "search query must trim surrounding whitespace");
    }

    @Test
    void blankInputCreatesInactiveQuery() {
        DocumentSearchQuery query = DocumentSearchQuery.from("   ");

        TestAssertions.assertFalse(query.isActive(), "blank search input must not start WebView search");
    }

    @Test
    void nullInputCreatesInactiveQuery() {
        DocumentSearchQuery query = DocumentSearchQuery.from(null);

        TestAssertions.assertFalse(query.isActive(), "null search input must not start WebView search");
    }

    @Test
    void readableInputCreatesActiveQuery() {
        DocumentSearchQuery query = DocumentSearchQuery.from("markdown");

        TestAssertions.assertTrue(query.isActive(), "readable search input must start WebView search");
    }
}
